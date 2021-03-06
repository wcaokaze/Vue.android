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

typealias V<T> = ReactiveField<T>

/**
 * An observable value.
 *
 * When the [value] of this ReactiveField is updated, observers are invoked.
 *
 * ```kotlin
 * val reactiveField: ReactiveField<Int> = state(0)
 *
 * var i = 0
 *
 * reactiveField.addObserver(fun (newValue: Int) {
 *    i = newValue
 * })
 *
 * reactiveField.value = 1
 *
 * assertEquals(1, i)
 * ```
 * Yes, this is a simple Observer-pattern.
 *
 * But wait, in Vue.android, we don't have to [add Observer][addObserver].
 * Vue.android provides a more elegant feature called [Reactivatee].
 * ```kotlin
 * val user = state<User?>(null)
 *
 * usernameTextView.vBind.text { user()?.name }
 * //                                ^
 * // Here, () means "getting the current value of a ReactiveField 'user',
 * // and adding THIS LAMBDA ITSELF into a ReactiveField 'user' as an observer."
 * ```
 *
 * @see Reactivatee
 */
interface ReactiveField<out T> {
   /**
    * The current value of this ReactiveField.
    *
    * Do not get this value. Use [value] instead.
    *
    * Implementations for ReactiveField can ignore the warning about this deprecated.
    * ```kotlin
    * @Suppress("OverridingDeprecatedMember")
    * override val `$vueInternal$value`: T
    * ```
    */
   @Deprecated("Do not get this value. Use value instead", ReplaceWith("value", "vue.*"))
   val `$vueInternal$value`: T

   val observerCount: Int

   /**
    * add an observer for the [value] of this ReactiveField.
    *
    * Observers are not duplicated, meaning that this function will ignore
    * for the same observer twice.
    *
    * Observers are called on the Android UI Thread.
    */
   @UiThread
   fun addObserver(@UiThread observer: (Result<T>) -> Unit)

   /**
    * remove an observer which was added via [addObserver].
    *
    * If the specified observer is not added to this ReactiveField,
    * this function do nothing.
    */
   @UiThread
   fun removeObserver(observer: (Result<T>) -> Unit)
}

/**
 * The current value of this ReactiveField.
 */
val <T> ReactiveField<T>.value: T
   get() {
      @Suppress("DEPRECATION")
      return `$vueInternal$value`
   }

/**
 * A shorthand for [value].
 * @return The current value of this ReactiveField
 */
operator fun <T> ReactiveField<T>.invoke(): T = value
