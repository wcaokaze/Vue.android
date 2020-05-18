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

   override val store: Store get() = application.store

   private val application by lazy { getApplication() as Application }

   @OptIn(ExperimentalContracts::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      val id = intent.getSerializableExtra(INTENT_KEY_STATUS_ID) as? Status.Id
      require(id != null) { "Status ID is not specified." }

      koshian(this) {
         componentView = LinearLayout {
            view.orientation = VERTICAL

            Component[StatusComponent, MASTODON] {
               layout.width  = MATCH_PARENT
               component.status { getter.modules[MASTODON].getStatus(id)() }
            }

            Component[FooterComponent] {
               layout.width = MATCH_PARENT
               layout.verticalMargin = 8.dip
            }
         }
      }

      setContentView(componentView)
   }
}
