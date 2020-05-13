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

package com.wcaokaze.vue.android.example.activity.status

import android.content.*
import android.graphics.*
import android.os.*
import android.text.*
import android.widget.*
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import vue.*
import vue.koshian.*
import java.text.*
import java.util.*
import kotlin.contracts.*

class StatusComponent(context: Context, state: State, getter: Getter) : VComponent() {
   override val componentView: LinearLayout

   private val accountComponent: AccountComponent

   private val contentView: TextView
   private val createdDateView: TextView

   val status = vBinder<Status?>()

   private val toot: V<Status.Toot?> = getter {
      when (val s = status()) {
         null            -> null
         is Status.Toot  -> s
         is Status.Boost -> s.toot
      }
   }

   private val tooter: V<Account?> = getter {
      val toot = toot() ?: return@getter null
      getter.modules[MASTODON].getAccount(toot.tooterAccountId)()
   }

   private val tootContent: V<Spannable?> = getter {
      val toot = toot() ?: return@getter null

      val builder = SpannableStringBuilder()

      if (!toot.spoilerText.isNullOrEmpty()) {
         builder
            .append(toot.spoilerText)
            .append("\n\n")
      }

      if (Build.VERSION.SDK_INT >= 24) {
         builder.append(
            Html.fromHtml(toot.content, Html.FROM_HTML_MODE_COMPACT))
      } else {
         builder.append(
            @Suppress("DEPRECATION")
            Html.fromHtml(toot.content))
      }

      builder
   }

   private val tootedDateStr: V<String?> = getter {
      val createdDate = toot()?.tootedDate ?: return@getter null
      SimpleDateFormat("HH:mm MMM d yyyy", Locale.US).format(createdDate)
   }

   init {
      @OptIn(ExperimentalContracts::class)
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = VERTICAL

            accountComponent = Component(AccountComponent(context, state, getter)) {
               component.account(tooter)
            }

            contentView = TextView {
               vBind.text(tootContent)
            }

            createdDateView = TextView {
               vBind.text(tootedDateStr)
            }
         }
      }

      componentView.applyKoshian {
         view.padding = 8.dip

         Component(accountComponent) {
            layout.width = MATCH_PARENT
         }

         contentView {
            layout.width = MATCH_PARENT
            layout.margins = 8.dip
            view.textColor = 0x000000.opaque
            view.textSizeSp = 15
            view.setLineSpacing(2.dip.toFloat(), 1.0f)
         }

         createdDateView {
            layout.gravity = END
            layout.horizontalMargin = 8.dip
            view.textColor = 0x000000 opacity 0.54
            view.textSizeSp = 12
         }
      }
   }
}
