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

class VBindProvider<out V : View>(val substance: V) {
   private var binders = emptyArray<Pair<Any, VBinder<*>>>()

   @UiThread
   inline fun <T> createVBinder(
         prop: KCallable<VBinder<T>>,
         crossinline binderAction: (view: V, value: T) -> Unit
   ): VBinder<T> {
      return createVBinder(prop) { value -> binderAction(substance, value) }
   }

   @UiThread
   fun <T> createVBinder(
         prop: KCallable<VBinder<T>>,
         binderAction: (value: T) -> Unit
   ): VBinder<T> {
      for ((p, b) in binders) {
         if (p == prop) {
            @Suppress("UNCHECKED_CAST")
            return b as VBinder<T>
         }
      }

      val b = ViewBinder(substance, binderAction)
      binders += prop to b
      return b
   }
}

@get:UiThread
val <V : View> V.vBind: VBindProvider<V> get() {
   val cachedProvider = getTag(R.id.view_tag_v_bind_provider)

   if (cachedProvider != null) {
      @Suppress("UNCHECKED_CAST")
      return cachedProvider as VBindProvider<V>
   }

   val newProvider = VBindProvider(this)
   setTag(R.id.view_tag_v_bind_provider, newProvider)
   return newProvider
}
