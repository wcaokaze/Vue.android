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
 *       is StatusItem -> ViewHolderProvider(item) {
 *          val status = getter { reactiveItem().status }
 *          val formatter = DateTimeFormatter.ofFormat("d MMM yyyy HH:mm")
 *          val formattedCreatedTime = getter { formatter.format(status().createdTime) }
 *
 *          val userComponent: UserComponent
 *
 *          koshian(context) {
 *             LinearLayout {
 *                view.orientation = VERTICAL
 *
 *                userComponent = Component(UserComponent(context)) {
 *                   component.user { status().user }
 *                }
 *
 *                TextView {
 *                   vBind.text { status().content }
 *                }
 *
 *                TextView {
 *                   vBind.text { formattedCreatedTime() }
 *                }
 *             }
 *          }
 *       }
 *
 *       is LoadingIndicatorItem -> ViewHolderProvider(item) {
 *          koshian(context) {
 *             FrameLayout {
 *                layout.height = 48.dp
 *
 *                ProgressBar {
 *                   layout.gravity = CENTER
 *                }
 *             }
 *          }
 *       }
 *    }
 * }
 * ```
 */
@Suppress("FunctionName")
inline fun <I> VueHolderProvider(
      item: I,
      crossinline itemViewCreatorAction: VueHolder<I>.() -> View
): ViewHolderProvider<I> {
   return object : ViewHolderProvider<I> {
      override fun provide(context: Context): KoshianViewHolder<I> {
         return object : VueHolder<I>(context, item) {
            override val itemView: View = itemViewCreatorAction(this)
         }
      }
   }
}

abstract class VueHolder<I>(val context: Context, initialItem: I) : KoshianViewHolder<I>() {
   val reactiveItem = state(initialItem)

   final override fun bind(item: I) {
      reactiveItem.value = item
   }
}
