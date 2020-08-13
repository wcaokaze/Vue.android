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
 * Two-way data binding.
 *
 * The property will be updated when the bound [StateField] is updated.
 * The [StateField] will be updated when the bound property is updated.
 *
 * ```kotlin
 * val vModel = editText.vModel.text
 * val state = state("Initial value")
 * vModel.bind(state)
 *
 * state.value = "a new string"
 * assertEquals("a new string", editText.text.toString())
 *
 * editText.setText("a new string 2")
 * assertEquals("a new string 2", state.value)
 * ```
 */
interface VModel<in I, out O> {
   @UiThread fun bind(input: StateField<out I>, output: StateField<in O>)
}

@UiThread
fun <I, O, T>
      VModel<I, O>.bind(state: StateField<T>)
      where T : I, O : T
{
   bind(state, state)
}

/**
 * A shorthand for [bind]
 */
@UiThread
operator fun <I, O, T>
      VModel<I, O>.invoke(state: StateField<T>)
      where T : I, O : T
{
   bind(state, state)
}
