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

import androidx.recyclerview.widget.RecyclerView
import koshian.*
import koshian.recyclerview.*
import vue.*
import vue.koshian.*

class RecyclerViewAdapterVBindProvider<I>(
   val recyclerViewVBindProvider: VBindProvider<RecyclerView>,
   val adapter: KoshianRecyclerViewAdapter<I>
)

/**
 * `vBind` for [KoshianRecyclerViewAdapter].
 *
 * The adapter is set to this RecyclerView.
 */
fun <I> RecyclerView.vBind(
      adapter: KoshianRecyclerViewAdapter<I>
): RecyclerViewAdapterVBindProvider<I> {
   this@vBind.adapter = adapter
   return RecyclerViewAdapterVBindProvider(vBind, adapter)
}

/**
 * `vBind` for [KoshianRecyclerViewAdapter].
 *
 * The adapter is set to this RecyclerView.
 */
fun <I> Koshian<RecyclerView, *, *, *>.vBind(
   adapter: KoshianRecyclerViewAdapter<I>
): RecyclerViewAdapterVBindProvider<I> {
   view.adapter = adapter
   return RecyclerViewAdapterVBindProvider(vBind, adapter)
}

val <I> RecyclerViewAdapterVBindProvider<I>.items: VBinder<List<I>>
   get() = recyclerViewVBindProvider.createVBinder(::items) { _, value ->
      adapter.items = value
   }

fun <I> VBindProvider<RecyclerView>
      .items(adapter: KoshianRecyclerViewAdapter<I>): VBinder<List<I>>
{
   substance.adapter = adapter

   return createVBinder(::items) { _, value ->
      adapter.items = value
   }
}
