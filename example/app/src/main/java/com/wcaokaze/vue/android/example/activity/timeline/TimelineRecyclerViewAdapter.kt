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

import android.content.*
import android.graphics.*
import android.os.*
import android.text.*
import android.widget.*
import com.wcaokaze.vue.android.example.R
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

   override fun equals(other: Any?)
         = other is StatusItem && other.statusId == statusId

   override fun hashCode() = statusId.hashCode()

   override fun toString() = "StatusItem(statusId = $statusId)"
}

object LoadingIndicatorItem : TimelineRecyclerViewItem() {
   override fun isContentsTheSameWith(item: Any) = item is LoadingIndicatorItem
   override fun isItemsTheSameWith(item: Any) = item is LoadingIndicatorItem

   override fun equals(other: Any?) = other is LoadingIndicatorItem
   override fun hashCode() = 0
   override fun toString() = "LoadingIndicatorItem"
}

object MissingStatusItem : TimelineRecyclerViewItem() {
   override fun isContentsTheSameWith(item: Any) = item is MissingStatusItem
   override fun isItemsTheSameWith(item: Any) = item is MissingStatusItem

   override fun equals(other: Any?) = other is MissingStatusItem
   override fun hashCode() = 0
   override fun toString() = "MissingStatusItem"
}

// =============================================================================

class TimelineRecyclerViewAdapter(context: Context,
                                  override val store: MastodonStore)
   : RecyclerViewAdapterComponent<TimelineRecyclerViewItem, MastodonStore>(context)
{
   val onItemClick = vEvent2<Int, TimelineRecyclerViewItem>()

   @OptIn(ExperimentalContracts::class)
   override fun selectViewHolderProvider(
      position: Int, item: TimelineRecyclerViewItem): ViewHolderProvider<*>
   = when (item) {
      is StatusItem -> VueHolderProvider(item) {
         val status: V<Status?> = getter {
            val id = reactiveItem().statusId
            getter.getStatus(id)()
         }

         val toot: V<Status.Toot?> = getter {
            when (val s = status()) {
               null            -> null
               is Status.Toot  -> s
               is Status.Boost -> s.toot
            }
         }

         val tooter: V<Account?> = getter {
            val id = toot()?.tooterAccountId ?: return@getter null
            getter.getAccount(id)()
         }

         val tooterIcon: V<Bitmap?> = getter {
            val id = toot()?.tooterAccountId ?: return@getter null
            getter.getAccountIcon(id)()
         }

         val boost: V<Status.Boost?> = getter { status() as? Status.Boost }

         val booster: V<Account?> = getter {
            val id = boost()?.boosterAccountId ?: return@getter null
            getter.getAccount(id)()
         }

         val boosterIcon: V<Bitmap?> = getter {
            val id = boost()?.boosterAccountId ?: return@getter null
            getter.getAccountIcon(id)()
         }

         val tootContent: V<Spannable?> = getter {
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

         val tootedDateStr: V<String?> = getter {
            val createdDate = toot()?.tootedDate ?: return@getter null
            SimpleDateFormat("HH:mm MMM d yyyy", Locale.US).format(createdDate)
         }

         // --------

         val iconView: ImageView
         val usernameView: TextView
         val acctView: TextView

         val contentView: TextView

         val createdDateView: TextView

         val boosterIconView: ImageView
         val boosterNameView: TextView

         val itemView = koshian(context) {
            LinearLayout {
               view.orientation = HORIZONTAL
               vOn.click { onItemClick.emit(adapterPosition, reactiveItem()) }

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
         }

         itemView.applyKoshian {
            layout.width = MATCH_PARENT
            view.padding = 8.dp

            iconView {
               layout.width  = 40.dp
               layout.height = 40.dp
               layout.margins = 4.dp
            }

            LinearLayout {
               layout.width = MATCH_PARENT
               layout.margins = 4.dp

               LinearLayout {
                  layout.width = MATCH_PARENT

                  usernameView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 2.dp
                     layout.verticalMargin   = 4.dp
                     view.textColor = 0x2196f3.opaque
                     view.typeface = BOLD
                     view.maxLines = 1
                     view.textSizeSp = 14
                  }

                  acctView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 2.dp
                     layout.verticalMargin   = 4.dp
                     view.textColor = 0x000000 opacity 0.54
                     view.maxLines = 1
                     view.textSizeSp = 13
                  }
               }

               contentView {
                  layout.width = MATCH_PARENT
                  layout.margins = 8.dp
                  view.textColor = 0x000000.opaque
                  view.textSizeSp = 13
                  view.setLineSpacing(2.dp.toFloat(), 1.0f)
               }

               createdDateView {
                  layout.gravity = END
                  view.textColor = 0x000000 opacity 0.54
                  view.textSizeSp = 11
               }

               LinearLayout {
                  ImageView {
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 8.dp
                  }

                  boosterIconView {
                     layout.width  = 24.dp
                     layout.height = 24.dp
                     layout.gravity = CENTER_VERTICAL
                     layout.horizontalMargin = 4.dp
                  }

                  boosterNameView {
                     layout.width  = MATCH_PARENT
                     layout.height = WRAP_CONTENT
                     layout.horizontalMargin = 4.dp
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

      // =======================================================================

      is LoadingIndicatorItem -> VueHolderProvider(item) {
         koshian(context) {
            FrameLayout {
               layout.width = MATCH_PARENT

               ProgressBar {
                  layout.width  = 32.dp
                  layout.height = 32.dp
                  layout.gravity = CENTER_HORIZONTAL
                  layout.margins = 8.dp
               }
            }
         }
      }

      // =======================================================================

      is MissingStatusItem -> VueHolderProvider(item) {
         koshian(context) {
            FrameLayout {
               layout.width = MATCH_PARENT

               ImageView {
                  layout.gravity = CENTER_HORIZONTAL
                  layout.margins = 8.dp
                  view.image = drawable(R.drawable.timeline_missing_item)
               }
            }
         }
      }
   }
}
