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
 * The most basic [ReactiveField] which stores one value.
 *
 * ### Initialization
 * ```kotlin
 * val user = state<User?>(null)
 * ```
 *
 * ### Getting the Value
 * ```kotlin
 * user.value
 *
 * // or shorter
 * user()
 * ```
 *
 * ### Reassignment
 * ```kotlin
 * user.value = User(...)
 * ```
 */
fun <T> state(initialValue: T) = StateField(StateImpl(initialValue))

/**
 * The most basic [ReactiveField] which stores one value.
 *
 * ### Initialization
 * ```kotlin
 * val user = state<User?>(null)
 * ```
 *
 * ### Getting the Value
 * ```kotlin
 * user.value
 *
 * // or shorter
 * user()
 * ```
 *
 * ### Reassignment
 * ```kotlin
 * user.value = User(...)
 * ```
 */
class StateField<T>
      internal constructor(private val delegate: StateImpl<T>)
      : ReactiveField<T> by delegate
{
   var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value
      }
}
