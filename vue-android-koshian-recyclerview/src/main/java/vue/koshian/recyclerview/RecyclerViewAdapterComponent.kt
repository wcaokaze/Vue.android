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

package vue.koshian.recyclerview

import android.content.*
import androidx.recyclerview.widget.*
import koshian.*
import koshian.recyclerview.*
import vue.*
import vue.koshian.*

abstract class RecyclerViewAdapterComponent<I>(context: Context)
   : KoshianRecyclerViewAdapter<I>(), VComponentInterface
{
   @Suppress("LeakingThis")
   final override val componentLifecycle = ComponentLifecycle(this)

   final override val componentView = RecyclerView(context)

   @Suppress("LeakingThis")
   val itemsBinder = vBinder<List<I>>()

   init {
      @Suppress("LeakingThis")
      componentView.adapter = this

      val itemsViewBinder = ViewBinder(componentView, fun (v: Result<List<I>>) {
         items = v.getOrThrow()
      })

      itemsViewBinder { itemsBinder() ?: emptyList() }
   }
}

inline val Koshian<RecyclerViewAdapterComponent<*>, *, *, *>.view: RecyclerView
   get() = component.componentView
