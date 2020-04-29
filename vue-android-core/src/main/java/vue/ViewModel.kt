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
import androidx.annotation.*

class ViewModel<V : View, in I, out O>(
      view: V,
      private val viewPropertyChangeEventListenerSetter: (onChanged: (O) -> Unit) -> Unit,
      private val viewBinder: (Result<I>) -> Unit
) : VModel<I, O> {
   private var isBinding = false
   private var inputField:  StateField<out I>? = null
   private var outputField: StateField<in  O>? = null

   private var viewProp: O? = null

   private val onAttachStateChange = object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View?) {
         bind()
      }

      override fun onViewDetachedFromWindow(v: View?) {
         unbind()
      }
   }

   private val onViewPropChanged = fun (newValue: O) {
      viewProp = newValue
      outputField?.value = newValue
   }

   private val onStateChanged = fun (newValue: Result<I>) {
      if (newValue.getOrNull() === viewProp) { return }
      viewBinder(newValue)
   }

   init {
      view.addOnAttachStateChangeListener(onAttachStateChange)
   }

   override fun bind(input: StateField<out I>, output: StateField<in O>) {
      viewPropertyChangeEventListenerSetter(onViewPropChanged)

      if (isBinding) {
         unbind()
         inputField  = input
         outputField = output
         bind()
      } else {
         inputField  = input
         outputField = output
      }
   }

   @UiThread
   private fun bind() {
      if (isBinding) { return }
      isBinding = true

      val input = inputField ?: return
      input.addObserver(onStateChanged)

      val currentValue: Result<I> = try {
         Result.success(input.value)
      } catch (e: Throwable) {
         Result.failure(e)
      }

      viewBinder(currentValue)
   }

   @UiThread
   private fun unbind() {
      if (!isBinding) { return }
      isBinding = false

      inputField?.removeObserver(onStateChanged)
   }
}
