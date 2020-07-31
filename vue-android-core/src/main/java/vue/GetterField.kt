/*
 * Copyright 2020 wcaokaze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vue

import androidx.annotation.*

/**
 * computes a value from other [ReactiveField]s. This is also a [ReactiveField].
 * ```kotlin
 * val user = state<User?>(null)
 * val username = getter { user()?.name }
 * ```
 * ```kotlin
 * assertEquals(null, username.value)
 * user.value = User(name = "wcaokaze")
 * assertEquals("wcaokaze", username.value)
 * ```
 */
fun <T> getter(reactivatee: ReactivateeScope.() -> T) = GetterField(reactivatee)

/**
 * computes a value from other [ReactiveField]s. This is also a [ReactiveField].
 * ```kotlin
 * val user = state<User?>(null)
 * val username = getter { user()?.name }
 * ```
 * ```kotlin
 * assertEquals(null, username.value)
 * user.value = User(name = "wcaokaze")
 * assertEquals("wcaokaze", username.value)
 * ```
 */
class GetterField<out T>
      internal constructor(
            @UiThread internal val reactivatee: ReactivateeScopeImpl.() -> T
      )
      : ReactiveField<T>
{
   private val upstreamObserver = fun (_: Any?) {
      val prevValue = currentValueCache

      invokeReactivatee()
      val newValue = currentValueCache

      if (newValue != prevValue) {
         notifyObservers(newValue)
      }
   }

   private var isBoundToUpstream = false

   /**
    * the value of GetterField doesn't not change while [isBoundToUpstream] == true.
    * This prop stores a cache of the value.
    */
   private var currentValueCache: Result<T>
         = Result.failure(Exception("GetterField has not been initialized yet"))

   private val dependeeFields = HashSet<Any>()

   private var downstreams: Array<((Result<T>) -> Unit)> = emptyArray()

   override val observerCount get() = downstreams.size

   /**
    * returns the cache of the current value if [isBoundToUpstream] == true.
    * Or calls [GetterField.reactivatee] if [isBoundToUpstream] == false.
    *
    * The type of this prop is `Any?` since ReactivateeScope should not have any
    * type parameters. But this value can casted to `GetterField.T` safely.
    */
   internal fun getValue(): Any? {
      if (!isBoundToUpstream) {
         invokeReactivatee()
      }

      return currentValueCache.getOrThrow()
   }

   @Suppress("OverridingDeprecatedMember")
   override val `$vueInternal$value`: T get() {
      @Suppress("UNCHECKED_CAST")
      return getValue() as T
   }

   override fun addObserver(observer: (Result<T>) -> Unit) {
      if (containsObserver(observer)) { return }

      val shouldBind = observerCount == 0

      downstreams += observer

      if (shouldBind) {
         bindToDependees()
      }
   }

   override fun removeObserver(observer: (Result<T>) -> Unit) {
      val a = downstreams

      when (a.size) {
         0 -> return

         1 -> {
            if (a[0] === observer) {
               downstreams = emptyArray()
               unbindFromDependees()
            }

            return
         }

         else -> {
            var i = 0

            while (true) {
               if (i >= a.size) { return }
               if (a[i] === observer) { break }
               i++
            }

            val newL = a.size - 1
            val newA = arrayOfNulls<(Result<T>) -> Unit>(newL)

            System.arraycopy(a,     0, newA, 0, i)
            System.arraycopy(a, i + 1, newA, i, newL - i)

            @Suppress("UNCHECKED_CAST")
            downstreams = newA as Array<(Result<T>) -> Unit>
         }
      }
   }

   internal fun startToObserve(field: ReactiveField<*>) {
      if (field in dependeeFields) { return }

      if (isBoundToUpstream) {
         field.addObserver(upstreamObserver)
         dependeeFields += field
      }
   }

   internal fun startToObserve(vBinder: VComponentInterface.ComponentVBinder<*>) {
      if (vBinder in dependeeFields) { return }

      if (isBoundToUpstream) {
         vBinder.field.addObserver(upstreamObserver)
         dependeeFields += vBinder
      }
   }

   private fun stopObserving(field: ReactiveField<*>) {
      if (field !in dependeeFields) { return }

      field.removeObserver(upstreamObserver)
      dependeeFields -= field
   }

   private fun stopObserving(vBinder: VComponentInterface.ComponentVBinder<*>) {
      if (vBinder !in dependeeFields) { return }

      vBinder.field.removeObserver(upstreamObserver)
      dependeeFields -= vBinder
   }

   private fun invokeReactivatee() {
      val reactivatee = reactivatee
      val reactivateeScope = ReactivateeScopeImpl(this)

      val newValue: Result<T> = try {
         Result.success(reactivateeScope.reactivatee())
      } catch (e: Throwable) {
         Result.failure(e)
      }

      currentValueCache = newValue

      removeNoLongerUsedObservers(reactivateeScope.calledReactiveFields)
   }

   private fun removeNoLongerUsedObservers(usedObservers: Set<Any>) {
      // make a clone since dependeeFields may be modified in removeObserver
      val dependeeFields = HashSet(dependeeFields)

      for (d in dependeeFields) {
         if (d in usedObservers) { continue }

         when (d) {
            is ReactiveField<*>                        -> stopObserving(d)
            is VComponentInterface.ComponentVBinder<*> -> stopObserving(d)
         }
      }
   }

   private fun notifyObservers(value: Result<T>) {
      val observers = downstreams
      val observerCount = observerCount

      for (i in 0 until observerCount) {
         observers[i].invoke(value)
      }
   }

   private fun containsObserver(observer: (Result<T>) -> Unit): Boolean {
      for (o in downstreams) {
         if (o === observer) { return true }
      }

      return false
   }

   @UiThread
   private fun bindToDependees() {
      // make a clone since dependeeFields may be modified in addObserver
      val dependeeFields = HashSet(dependeeFields)

      for (d in dependeeFields) {
         when (d) {
            is ReactiveField<*> -> d.addObserver(upstreamObserver)
            is VComponentInterface.ComponentVBinder<*> -> d.field.addObserver(upstreamObserver)
         }
      }

      isBoundToUpstream = true
      invokeReactivatee()
   }

   @UiThread
   private fun unbindFromDependees() {
      isBoundToUpstream = false

      // make a clone since dependeeFields may be modified in removeObserver
      val dependeeFields = HashSet(dependeeFields)

      for (d in dependeeFields) {
         when (d) {
            is ReactiveField<*> -> d.removeObserver(upstreamObserver)
            is VComponentInterface.ComponentVBinder<*> -> d.field.removeObserver(upstreamObserver)
         }
      }
   }
}

/**
 * Function which will be re-invoked when any depending ReactiveField is updated.
 *
 * ```kotlin
 * val user = state<User?>(null)
 *
 * //                    This lambda is a Reactivatee.
 * //                      v~~~~~~~~~~~~~~~
 * usernameView.vBind.text { user()?.name }
 * //                        ^~~~~~
 * //                Getting the value.
 * //                And now this Reactivatee depends on a ReactiveField 'user'.
 * //                This Reactivatee will be re-invoked when 'user' is updated.
 *
 *
 * user.value = User(name = "wcaokaze")
 * assertEquals("wcaokaze", usernameView.text.toString())
 *
 * user.value = User(name = "Vue.android")
 * assertEquals("Vue.android", usernameView.text.toString())
 * ```
 */
typealias Reactivatee<T> = ReactivateeScope.() -> T

/**
 * @see Reactivatee
 */
interface ReactivateeScope {
   /**
    * [adds][ReactiveField.addObserver] the current [Reactivatee] as an observer
    * for this ReactiveField, and returns the current value of this ReactiveField.
    */
   @get:UiThread
   val <T> ReactiveField<T>.value: T

   /**
    * A shorthand for [value].
    *
    * [adds][ReactiveField.addObserver] the current [Reactivatee] as an observer
    * for this ReactiveField, and returns the current value of this ReactiveField.
    *
    * @return The current value of this ReactiveField
    */
   @UiThread
   operator fun <T> ReactiveField<T>.invoke(): T = value
}

internal class ReactivateeScopeImpl(private val getterField: GetterField<*>)
      : ReactivateeScope, VComponentInterface.ComponentReactivateeScope
{
   val calledReactiveFields = HashSet<Any>()

   override val <T> ReactiveField<T>.value: T get() {
      getterField.startToObserve(this)
      calledReactiveFields += this

      @Suppress("DEPRECATION")
      return `$vueInternal$value`
   }

   override val <T> VComponentInterface.ComponentVBinder<T>.value: T? get() {
      getterField.startToObserve(this)
      calledReactiveFields += this

      return field.value
   }

   override fun toString() = getterField.toString()
}
