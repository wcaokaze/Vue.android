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

package com.wcaokaze.vue.android.example.activity.timeline

import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.R
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import koshian.recyclerview.*
import vue.*
import vue.koshian.*
import vue.koshian.recyclerview.*
import kotlin.contracts.*

sealed class TimelineRecyclerViewItem : DiffUtilItem

class StatusItem(val statusId: Status.Id) : TimelineRecyclerViewItem() {
   override fun isContentsTheSameWith(item: Any)
         = item is StatusItem && item.statusId == statusId

   override fun isItemsTheSameWith(item: Any)
         = item is StatusItem && item.statusId == statusId
}

object LoadingIndicatorItem : TimelineRecyclerViewItem() {
   override fun isContentsTheSameWith(item: Any) = item is LoadingIndicatorItem
   override fun isItemsTheSameWith(item: Any) = item is LoadingIndicatorItem
}

object MissingStatusItem : TimelineRecyclerViewItem() {
   override fun isContentsTheSameWith(item: Any) = item is MissingStatusItem
   override fun isItemsTheSameWith(item: Any) = item is MissingStatusItem
}

class TimelineRecyclerViewAdapter(private val state: State,
                                  private val getter: Getter)
   : KoshianRecyclerViewAdapter<TimelineRecyclerViewItem>()
{
   @OptIn(ExperimentalContracts::class)
   override fun selectViewHolderProvider(
      position: Int, item: TimelineRecyclerViewItem): ViewHolderProvider<*>
   = when (item) {
      is StatusItem -> VueHolderProvider(item) { reactiveItem ->
         val status = getter {
            val id = reactiveItem().statusId
            getter.modules[MASTODON].getStatus(id)()
         }

         val toot = getter {
            when (val s = status()) {
               null            -> null
               is Status.Toot  -> s
               is Status.Boost -> s.toot
            }
         }

         TextView {
            layout.width = MATCH_PARENT
            vBind.text { toot()?.content }
         }
      }

      is LoadingIndicatorItem -> VueHolderProvider(item) {
         FrameLayout {
            layout.width = MATCH_PARENT

            ProgressBar {
               layout.width  = 32.dip
               layout.height = 32.dip
               layout.gravity = CENTER_HORIZONTAL
               layout.margins = 8.dip
            }
         }
      }

      is MissingStatusItem -> VueHolderProvider(item) {
         FrameLayout {
            layout.width = MATCH_PARENT

            ImageView {
               layout.gravity = CENTER_HORIZONTAL
               layout.margins = 8.dip
               view.image = drawable(R.drawable.timeline_missing_item)
            }
         }
      }
   }
}
