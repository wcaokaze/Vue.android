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

import android.view.*

class ViewModel<V : View, in I, out O>(
      view: V,
      viewPropertyChangeEventListenerSetter: (onChanged: (O) -> Unit) -> Unit,
      private val viewBinder: (Result<I>) -> Unit
) : VModel<I, O> {
   private var inputField:  StateField<out I>? = null
   private var outputField: StateField<in  O>? = null

   private var viewProp: O? = null

   private val onViewPropChanged = fun (newValue: O) {
      viewProp = newValue
      outputField?.value = newValue
   }

   private val onStateChanged = fun (newValue: Result<I>) {
      if (newValue.getOrNull() === viewProp) { return }
      viewBinder(newValue)
   }

   override fun bind(input: StateField<out I>, output: StateField<in O>) {
      inputField?.removeObserver(onStateChanged)

      inputField  = input
      outputField = output

      input.addObserver(onStateChanged)
   }

   init {
      viewPropertyChangeEventListenerSetter(onViewPropChanged)
   }
}
