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

fun <T> getter(reactivatee: ReactivateeScope.() -> T) = GetterField(reactivatee)

class GetterField<out T>
      internal constructor(
            @UiThread internal val reactivatee: ReactivateeScopeImpl.() -> T
      )
      : ReactiveField<T>
{
   private val upstreamObserver = fun (_: Any?) {
      val reactivatee = reactivatee
      val newValue = reactivateeScope.reactivatee()
      currentValueCache = newValue
      notifyObservers(newValue)
   }

   private var isBoundToUpstream = false

   /**
    * the value of GetterField doesn't not change while [isBoundToUpstream] == true.
    * This prop stores a cache of the value.
    */
   private var currentValueCache: Any? = null

   private val dependeeFields = HashSet<Any>()

   private var downstreams: Array<((T) -> Unit)?> = arrayOfNulls(2)

   private val reactivateeScope = ReactivateeScopeImpl(this)

   override var observerCount = 0
      private set

   /**
    * returns the cache of the current value if [isBoundToUpstream] == true.
    * Or calls [GetterField.reactivatee] if [isBoundToUpstream] == false.
    *
    * The type of this prop is `Any?` since ReactivateeScope should not have any
    * type parameters. But this value can casted to `GetterField.T` safely.
    */
   internal fun getValue(): Any? {
      return if (isBoundToUpstream) {
         currentValueCache
      } else {
         val reactivatee = reactivatee
         reactivateeScope.reactivatee()
      }
   }

   @Suppress("OverridingDeprecatedMember")
   override val `$vueInternal$value`: T get() {
      @Suppress("UNCHECKED_CAST")
      return getValue() as T
   }

   override fun addObserver(observer: (T) -> Unit) {
      if (containsObserver(observer)) { return }

      val shouldBind = observerCount == 0

      if (observerCount >= downstreams.size) {
         downstreams = downstreams.copyOf(newSize = observerCount * 2)
      }

      downstreams[observerCount++] = observer

      if (shouldBind) {
         bindToDependees()
      }
   }

   override fun removeObserver(observer: (T) -> Unit) {
      val observers = downstreams

      when (observerCount) {
         0 -> return

         1 -> {
            if (observers[0] === observer) {
               observers[0] = null
               observerCount = 0
               unbindFromDependees()
            }

            return
         }

         else -> {
            var i = 0

            while (true) {
               if (i >= observerCount) { return }
               if (observers[i] === observer) { break }
               i++
            }

            System.arraycopy(observers, i + 1, observers, i, observerCount - i - 1)
            observers[observerCount - 1] = null
            observerCount--
         }
      }
   }

   internal fun startToObserve(field: ReactiveField<*>) {
      if (field in dependeeFields) { return }

      dependeeFields += field

      if (isBoundToUpstream) {
         field.addObserver(upstreamObserver)
      }
   }

   internal fun startToObserve(vBinder: VComponentInterface.ComponentVBinder<*>) {
      if (vBinder in dependeeFields) { return }

      dependeeFields += vBinder

      if (isBoundToUpstream) {
         vBinder.field.addObserver(upstreamObserver)
      }
   }

   private fun notifyObservers(value: @UnsafeVariance T) {
      val observers = downstreams
      val observerCount = observerCount

      for (i in 0 until observerCount) {
         observers[i]?.invoke(value)
      }
   }

   private fun containsObserver(observer: (T) -> Unit): Boolean {
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

      val reactivatee = reactivatee
      currentValueCache = reactivateeScope.reactivatee()
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
 * val username: V<String?>
 *    = getter { // this lambda is a Reactivatee.
 *
 *       user()?.toString()
 *       //  ^
 *       //  Getting the value.
 *       //  And now this Reactivatee depends on a ReactiveField 'user'.
 *       //  This Reactivatee will be re-invoked when 'user' is updated.
 *    }
 *
 * val usernameLength = getter { username()?.length ?: 0 }
 *
 * fun someFunction() {
 *    user.value = User(name = "wcaokaze")
 *
 *    assert(username() == "wcaokaze")
 *    assert(usernameLength() == 8)
 * }
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
   override val <T> ReactiveField<T>.value: T get() {
      getterField.startToObserve(this)

      @Suppress("DEPRECATION")
      return `$vueInternal$value`
   }

   override val <T> VComponentInterface.ComponentVBinder<T>.value: T? get() {
      getterField.startToObserve(this)
      return field.value
   }
}
