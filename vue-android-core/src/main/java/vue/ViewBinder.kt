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

/**
 * [VBinder] for [View]s
 *
 * ```kotlin
 * val user = state<User?>(null)
 *
 * usernameView.vBind.text { user()?.name }
 * ```
 */
class ViewBinder<V : View, T>(view: V, private val binder: (Result<T>) -> Unit) : VBinder<T> {
   private var isBinding = false
   private var boundReactiveField: ReactiveField<T>? = null

   private val onAttachStateChange = object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View?) {
         bindToReactiveField()
      }

      override fun onViewDetachedFromWindow(v: View?) {
         unbindFromReactiveField()
      }
   }

   init {
      view.addOnAttachStateChangeListener(onAttachStateChange)
   }

   override fun bind(reactiveField: ReactiveField<T>) {
      if (isBinding) {
         unbindFromReactiveField()
         boundReactiveField = reactiveField
         bindToReactiveField()
      } else {
         boundReactiveField = reactiveField
      }
   }

   override fun bind(nonReactiveValue: T) {
      unbindFromReactiveField()
      binder(Result.success(nonReactiveValue))
   }

   @UiThread
   private fun bindToReactiveField() {
      val boundReactiveField = boundReactiveField ?: return

      if (isBinding) { return }
      isBinding = true

      boundReactiveField.addObserver(binder)

      val currentValue: Result<T> = try {
         Result.success(boundReactiveField.value)
      } catch (e: Throwable) {
         Result.failure(e)
      }

      binder(currentValue)
   }

   @UiThread
   private fun unbindFromReactiveField() {
      if (!isBinding) { return }
      isBinding = false

      boundReactiveField?.removeObserver(binder)
   }
}
