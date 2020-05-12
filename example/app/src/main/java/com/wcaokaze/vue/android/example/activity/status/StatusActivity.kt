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
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import org.kodein.di.*
import org.kodein.di.android.*
import vue.*
import kotlin.contracts.*

class StatusActivity : Activity(), VComponentInterface, KodeinAware {
   companion object {
      const val INTENT_KEY_STATUS_ID = "GGZbOLON4U8o9Qp8"
   }

   override val kodein by closestKodein()
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: LinearLayout

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      val id = intent.getSerializableExtra(INTENT_KEY_STATUS_ID) as? Status.Id
      require(id != null) { "Status ID is not specified." }
   }

   private fun buildContentView() {
      @OptIn(ExperimentalContracts::class)
      koshian(this) {
         componentView = LinearLayout {
         }
      }

      setContentView(componentView)
   }
}
