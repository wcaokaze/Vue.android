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
import android.widget.*
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import vue.*
import vue.koshian.*
import kotlin.contracts.*

class AccountComponent(context: Context, override val store: MastodonStore)
   : VComponent<MastodonStore>()
{
   companion object : KoshianComponentConstructor<AccountComponent, MastodonStore> {
      override fun instantiate(context: Context, store: MastodonStore)
            = AccountComponent(context, store)
   }

   override val componentView: LinearLayout

   private val iconView: ImageView
   private val usernameView: TextView
   private val acctView: TextView

   val account = vBinder<Account?>()

   private val icon: V<Bitmap?> = getter {
      val account = account() ?: return@getter null
      getter.getAccountIcon(account.id)()
   }

   init {
      @OptIn(ExperimentalContracts::class)
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = HORIZONTAL

            iconView = ImageView {
               vBind.imageBitmap(icon)
            }

            LinearLayout {
               view.orientation = VERTICAL

               usernameView = TextView {
                  vBind.text { account()?.name }
               }

               acctView = TextView {
                  vBind.text {
                     val acct = account()?.acct
                     if (acct != null) { "@$acct" } else { null }
                  }
               }
            }
         }
      }

      componentView.applyKoshian {
         iconView {
            layout.width  = 48.dp
            layout.height = 48.dp
            layout.margins = 8.dp
         }

         LinearLayout {
            layout.width = MATCH_PARENT
            layout.gravity = CENTER_VERTICAL

            usernameView {
               layout.width = MATCH_PARENT
               layout.gravity = CENTER_VERTICAL
               layout.horizontalMargin = 2.dp
               layout.verticalMargin   = 2.dp
               view.textColor = 0x2196f3.opaque
               view.typeface = BOLD
               view.maxLines = 1
               view.textSizeSp = 15
            }

            acctView {
               layout.width = MATCH_PARENT
               layout.gravity = CENTER_VERTICAL
               layout.horizontalMargin = 2.dp
               layout.verticalMargin   = 2.dp
               view.textColor = 0x000000 opacity 0.54
               view.maxLines = 1
               view.textSizeSp = 14
            }
         }
      }
   }
}

