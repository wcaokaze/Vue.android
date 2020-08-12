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
import com.wcaokaze.vue.android.*
import kotlin.reflect.*

/**
 * provides [VModel]s.
 *
 * ```kotlin
 * //                      VModel
 * //                      v~~~
 * commentInputView.vModel.text(inputComment)
 * //               ^~~~~~
 * //               VModelProvider
 * ```
 */
class VModelProvider<out V : View>(val substance: V) {
   private var binders = emptyArray<Pair<Any, VModel<*, *>>>()

   @UiThread
   inline fun <I, O> createVModel(
         prop: KCallable<VModel<I, O>>,
         noinline viewPropertyChangeEventListenerSetter: (onChanged: (O) -> Unit) -> Unit,
         crossinline binderAction: (view: V, value: I) -> Unit
   ): VModel<I, O> {
      return createVModel(prop, viewPropertyChangeEventListenerSetter) { value ->
         binderAction(substance, value.getOrThrow())
      }
   }

   @UiThread
   fun <I, O> createVModel(
         prop: KCallable<VModel<I, O>>,
         viewPropertyChangeEventListenerSetter: (onChanged: (O) -> Unit) -> Unit,
         binderAction: (Result<I>) -> Unit
   ): VModel<I, O> {
      for ((p, b) in binders) {
         if (p == prop) {
            @Suppress("UNCHECKED_CAST")
            return b as VModel<I, O>
         }
      }

      val b = ViewModel(substance, viewPropertyChangeEventListenerSetter, binderAction)
      binders += prop to b
      return b
   }
}

@get:UiThread
val <V : View> V.vModel: VModelProvider<V> get() {
   val cachedProvider = getTag(R.id.view_tag_v_model_provider)

   if (cachedProvider != null) {
      @Suppress("UNCHECKED_CAST")
      return cachedProvider as VModelProvider<V>
   }

   val newProvider = VModelProvider(this)
   setTag(R.id.view_tag_v_model_provider, newProvider)
   return newProvider
}
