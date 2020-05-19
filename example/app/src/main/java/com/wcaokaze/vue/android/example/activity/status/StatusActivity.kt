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

import android.app.*
import android.os.*
import android.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import com.wcaokaze.vue.android.example.Application
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import kotlinx.coroutines.*
import org.kodein.di.*
import org.kodein.di.android.*
import vue.*
import vue.koshian.*
import kotlin.contracts.*

class StatusActivity : Activity(), VComponentInterface<Store>, KodeinAware {
   companion object {
      const val INTENT_KEY_STATUS_ID = "STATUS_ID"
   }

   override val kodein by closestKodein()
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: LinearLayout

   override val store: Store
      get() = (application as Application).store

   private val statusId = state<Status.Id?>(null)

   private val status: V<Status?> = getter {
      val statusId = statusId() ?: return@getter null
      getter.modules[MASTODON].getStatus(statusId)()
   }

   private val toot: V<Status.Toot?> = getter {
      when (val status = status()) {
         null            -> null
         is Status.Toot  -> status
         is Status.Boost -> status.toot
      }
   }

   private suspend fun onBoostButtonClick() {
      val statusId = statusId() ?: return

      try {
         if (toot()?.isBoosted == true) {
            action.modules[MASTODON].unboost(statusId)
         } else {
            action.modules[MASTODON].boost(statusId)
         }
      } catch (e: CancellationException) {
         throw e
      } catch (e: Exception) {
         Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show()
      }
   }

   private suspend fun onFavoriteButtonClick() {
      val statusId = statusId() ?: return

      try {
         if (toot()?.isFavorited == true) {
            action.modules[MASTODON].unfavorite(statusId)
         } else {
            action.modules[MASTODON].favorite(statusId)
         }
      } catch (e: CancellationException) {
         throw e
      } catch (e: Exception) {
         Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show()
      }
   }

   @OptIn(ExperimentalContracts::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      val id = intent.getSerializableExtra(INTENT_KEY_STATUS_ID) as? Status.Id
      require(id != null) { "Status ID is not specified." }
      statusId.value = id

      koshian(this) {
         componentView = LinearLayout {
            view.orientation = VERTICAL

            Component[StatusComponent, MASTODON] {
               layout.width  = MATCH_PARENT
               component.status(status)
            }

            Component[FooterComponent] {
               layout.width = MATCH_PARENT
               layout.verticalMargin = 8.dp
               component.isBoosted   { toot()?.isBoosted   }
               component.isFavorited { toot()?.isFavorited }
               component.onBoostButtonClick    { onBoostButtonClick()    }
               component.onFavoriteButtonClick { onFavoriteButtonClick() }
            }
         }
      }

      setContentView(componentView)
   }
}
