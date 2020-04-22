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
import android.view.*
import androidx.recyclerview.widget.*
import koshian.*
import koshian.recyclerview.*
import vue.*

/**
 * Finally we no longer have to write ViewHolders. All we have to do is
 * to write Koshian for every item.
 * ```kotlin
 * sealed class TimelineItem
 * class StatusItem(val status: Status) : TimelineItem()
 * object LoadingIndicatorItem : TimelineItem()
 *
 * class TimelineRecyclerViewAdapter : KoshianRecyclerViewAdapter<TimelineItem>() {
 *    override fun selectViewHolderProvider
 *          (position: Int, item: TimelineItem): ViewHolderProvider<*>
 *    = when (item) {
 *       is StatusItem -> ViewHolderProvider(item) { reactiveItem: ReactiveField<StatusItem> ->
 *          //                                                     ^~~~~~~~~~~~~
 *
 *          LinearLayout {
 *             view.orientation = VERTICAL
 *
 *             TextView {
 *                vBind.text { reactiveItem().status }
 *                //           ^~~~~~~~~~~~
 *             }
 *          }
 *       }
 *
 *       is LoadingIndicatorItem -> ViewHolderProvider(item) {
 *          FrameLayout {
 *             layout.height = 48.dip
 *
 *             ProgressBar {
 *                layout.gravity = CENTER
 *             }
 *          }
 *       }
 *    }
 * }
 * ```
 */
@Suppress("FunctionName")
inline fun <I> ViewHolderProvider(
      item: I,
      crossinline itemViewCreatorAction:
            Koshian<ViewManager, Nothing, RecyclerView.LayoutParams, KoshianMode.Creator>.(
                  reactiveItem: ReactiveField<I>
            ) -> View
): ViewHolderProvider<I> {
   return object : ViewHolderProvider<I> {
      override fun provide(context: Context): KoshianViewHolder<I> {
         val reactiveItem = state(item)

         val oldContext = `$$KoshianInternal`.context
         val oldParentConstructor = `$$KoshianInternal`.parentViewConstructor
         val oldApplyingIndex = `$$ApplierInternal`.applyingIndex
         val oldStyle = `$$StyleInternal`.style
         `$$KoshianInternal`.context = context
         `$$KoshianInternal`.parentViewConstructor = KoshianRecyclerViewRoot.CONSTRUCTOR
         `$$ApplierInternal`.applyingIndex = -1
         `$$StyleInternal`.style = null

         `$$KoshianInternal`.init(context)

         try {
            val koshian = Koshian<Nothing, Nothing, RecyclerView.LayoutParams, KoshianMode.Creator>(KoshianRecyclerViewRoot.INSTANCE)
            val itemView = koshian.itemViewCreatorAction(reactiveItem)
            return VueViewHolder(itemView, reactiveItem)
         } finally {
            `$$KoshianInternal`.context = oldContext
            `$$KoshianInternal`.parentViewConstructor = oldParentConstructor
            `$$ApplierInternal`.applyingIndex = oldApplyingIndex
            `$$StyleInternal`.style = oldStyle
         }
      }
   }
}

class VueViewHolder<I>(
      override val itemView: View,
      private val reactiveItem: StateField<I>
) : KoshianViewHolder<I>() {
   override fun bind(item: I) {
      reactiveItem.value = item
   }
}
