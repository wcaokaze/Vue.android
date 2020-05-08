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

import android.os.*
import android.text.*
import android.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.R
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import koshian.recyclerview.*
import vue.*
import vue.koshian.*
import vue.koshian.recyclerview.*
import java.text.SimpleDateFormat
import java.util.*
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

         val tooter = getter {
            val id = toot()?.tooterAccountId ?: return@getter null
            getter.modules[MASTODON].getAccount(id)()
         }

         val tooterIcon = getter {
            val id = toot()?.tooterAccountId ?: return@getter null
            getter.modules[MASTODON].getAccountIcon(id)()
         }

         val boost = getter { status() as? Status.Boost }

         val booster = getter {
            val id = boost()?.boosterAccountId ?: return@getter null
            getter.modules[MASTODON].getAccount(id)()
         }

         val boosterIcon = getter {
            val id = boost()?.boosterAccountId ?: return@getter null
            getter.modules[MASTODON].getAccountIcon(id)()
         }

         val tootContent = getter {
            @Suppress("NAME_SHADOWING")
            val toot = toot() ?: return@getter null

            val content = if (Build.VERSION.SDK_INT >= 24) {
               Html.fromHtml(toot.content, Html.FROM_HTML_MODE_COMPACT)
            } else {
               @Suppress("DEPRECATION")
               Html.fromHtml(toot.content)
            }

            val builder = SpannableStringBuilder()

            if (!toot.spoilerText.isNullOrEmpty()) {
               builder
                  .append(toot.spoilerText)
                  .append("\n\n")
            }

            builder.append(content)
            builder
         }

         val tootedDateStr = getter {
            val createdDate = toot()?.tootedDate ?: return@getter null
            SimpleDateFormat("HH:mm MMM d yyyy", Locale.US).format(createdDate)
         }

         val iconView: ImageView
         val usernameView: TextView
         val acctView: TextView

         val contentView: TextView

         val createdDateView: TextView

         val boosterIconView: ImageView
         val boosterNameView: TextView

         val itemView = LinearLayout {
            view.orientation = HORIZONTAL

            iconView = ImageView {
               vBind.imageBitmap(tooterIcon)
            }

            LinearLayout {
               view.orientation = VERTICAL

               LinearLayout {
                  view.orientation = HORIZONTAL

                  usernameView = TextView {
                     vBind.text { tooter()?.name }
                  }

                  acctView = TextView {
                     vBind.text {
                        val acct = tooter()?.acct
                        if (acct != null) { "@$acct" } else { null }
                     }
                  }
               }

               contentView = TextView {
                  vBind.text(tootContent)
               }

               createdDateView = TextView {
                  vBind.text(tootedDateStr)
               }

               LinearLayout {
                  view.orientation = HORIZONTAL
                  vBind.isOccupiable { boost() != null }

                  ImageView {
                     view.image = drawable(R.drawable.timeline_ic_boosted)
                  }

                  boosterIconView = ImageView {
                     vBind.imageBitmap(boosterIcon)
                  }

                  boosterNameView = TextView {
                     vBind.text {
                        val b = booster()
                        if (b != null) { "${b.name} - @${b.acct}" } else { null }
                     }
                  }
               }
            }
         }

         itemView.applyKoshian {
            layout.width = MATCH_PARENT
            view.padding = 8.dip

            iconView {
               layout.width  = 40.dip
               layout.height = 40.dip
               layout.margins = 4.dip
            }

            LinearLayout {
               layout.width = MATCH_PARENT
               layout.margins = 4.dip

               LinearLayout {
                  layout.width = MATCH_PARENT

                  usernameView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 2.dip
                     layout.verticalMargin   = 4.dip
                     view.textColor = 0x2196f3.opaque
                     view.typeface = BOLD
                     view.maxLines = 1
                     view.textSizeSp = 14
                  }

                  acctView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 2.dip
                     layout.verticalMargin   = 4.dip
                     view.textColor = 0x000000 opacity 0.54
                     view.maxLines = 1
                     view.textSizeSp = 13
                  }
               }

               contentView {
                  layout.width = MATCH_PARENT
                  layout.margins = 8.dip
                  view.textColor = 0x000000.opaque
                  view.textSizeSp = 13
                  view.setLineSpacing(2.dip.toFloat(), 1.0f)
               }

               createdDateView {
                  layout.gravity = END
                  view.textColor = 0x000000 opacity 0.54
                  view.textSizeSp = 11
               }

               LinearLayout {
                  ImageView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 8.dip
                  }

                  boosterIconView {
                     layout.width  = 24.dip
                     layout.height = 24.dip
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 4.dip
                  }

                  boosterNameView {
                     layout.width  = MATCH_PARENT
                     layout.height = WRAP_CONTENT
                     layout.horizontalMargin = 4.dip
                     layout.gravity = CENTER_VERTICAL
                     view.textColor = 0x000000 opacity 0.54
                     view.textSizeSp = 12
                     view.maxLines = 1
                     view.ellipsize = TRUNCATE_AT_END
                  }
               }
            }
         }

         itemView
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