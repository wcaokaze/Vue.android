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
import android.widget.*
import com.wcaokaze.vue.android.example.*
import koshian.*
import vue.*
import vue.koshian.*
import kotlin.contracts.*

class FooterComponent(context: Context) : VComponent<Nothing>() {
   companion object : KoshianNoStoreComponentConstructor<FooterComponent> {
      override fun instantiate(context: Context) = FooterComponent(context)
   }

   override val store: Nothing get() = throw UnsupportedOperationException()

   override val componentView: LinearLayout

   val isBoosted   = vBinder<Boolean?>()
   val isFavorited = vBinder<Boolean?>()

   val onBoostButtonClick    = vEvent0()
   val onFavoriteButtonClick = vEvent0()

   private val boostButtonDrawable = getter {
      val resId = if (isBoosted() == true) {
         R.drawable.status_btn_boost_on
      } else {
         R.drawable.status_btn_boost_off
      }

      context.getDrawable(resId)
   }

   private val favoriteButtonDrawable = getter {
      val resId = if (isFavorited() == true) {
         R.drawable.status_btn_favorite_on
      } else {
         R.drawable.status_btn_favorite_off
      }

      context.getDrawable(resId)
   }

   init {
      @OptIn(ExperimentalContracts::class)
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = HORIZONTAL

            ImageButton("button") {
               vOn.click(onBoostButtonClick)
               vBind.image { boostButtonDrawable() }
            }

            ImageButton("button") {
               vOn.click(onFavoriteButtonClick)
               vBind.image { favoriteButtonDrawable() }
            }
         }
      }

      componentView.applyKoshian {
         view.setPadding(horizontal = 16.dip, vertical = 0)

         ImageButton("button") {
            layout.width  = 0
            layout.height = 48.dip
            layout.weight = 2.0f
            layout.horizontalMargin = 8.dip
            view.scaleType = SCALE_TYPE_CENTER
         }
      }
   }
}
