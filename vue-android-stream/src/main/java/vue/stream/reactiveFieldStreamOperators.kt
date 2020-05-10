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

package vue.stream

import vue.*

inline fun <T, R> ReactiveField<T>.map(crossinline mapper: (T) -> R): ReactiveField<R> {
   return getter { mapper(this@map()) }
}

/**
 * @param initialValue
 *   The value while no values are available.
 *   For example:
 *   ```kotlin
 *   val state = state(1)
 *   val filtered = state.filter(0) { it <= 0 }
 *   state.value = 2
 *   state.value = 3
 *
 *   assertEquals(0, filtered.value)
 *   ```
 */
inline fun <T> ReactiveField<T>
      .filter(initialValue: T, crossinline filter: (T) -> Boolean): ReactiveField<T>
{
   var prevValue = initialValue

   return getter {
      val newValue = this@filter()

      if (filter(newValue)) {
         prevValue = newValue
         newValue
      } else {
         prevValue
      }
   }
}

inline fun <reified T, I> ReactiveField<*>
      .filterIsInstance(initialValue: I): ReactiveField<T>
      where I : T
{
   @Suppress("UNCHECKED_CAST")
   return filter(initialValue) { it is T } as ReactiveField<T>
}
