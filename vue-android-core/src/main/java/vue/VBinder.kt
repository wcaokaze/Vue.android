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
 * binds some property to a [ReactiveField].
 *
 * The bound property will be updated automatically.
 * ```kotlin
 * val textBinder: VBinder<CharSequence> = textView.vBind.text
 * val state = state("Initial value")
 *
 * textBinder.bind(state)
 * assertEquals("Initial value", textView.text.toString())
 *
 * state.value = "a new string"
 * assertEquals("a new string", textView.text.toString())
 * ```
 *
 * @see VComponentInterface.vBinder
 * @see ViewBinder
 */
interface VBinder<in T> {
   @UiThread fun bind(reactiveField: ReactiveField<T>)
   @UiThread fun bind(nonReactiveValue: T)

   /**
    * A shorthand for [bind]
    */
   @UiThread operator fun invoke(reactiveField: ReactiveField<T>) = bind(reactiveField)

   /**
    * A shorthand for [bind]
    */
   @UiThread operator fun invoke(nonReactiveValue: T) = bind(nonReactiveValue)
}

/**
 * A shorthand for [bind]
 */
@UiThread
operator fun <T> VBinder<T>.invoke(reactivatee: Reactivatee<T>) {
   bind(reactivatee)
}

/**
 * binds a [Reactivatee].
 *
 * This is equivalent to `bind(getter(reactivatee))`
 */
@UiThread
fun <T> VBinder<T>.bind(reactivatee: Reactivatee<T>) {
   val reactiveField = GetterField(reactivatee)
   invoke(reactiveField)
}
