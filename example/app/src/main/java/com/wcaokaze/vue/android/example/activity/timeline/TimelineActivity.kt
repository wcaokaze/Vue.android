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

import android.app.*
import android.os.*
import android.view.*
import androidx.recyclerview.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import koshian.*
import koshian.recyclerview.*
import kotlinx.coroutines.*
import org.kodein.di.*
import org.kodein.di.android.*
import vue.*
import vue.koshian.recyclerview.*
import kotlin.contracts.*

class TimelineActivity : Activity(), VComponentInterface, KodeinAware {
   override val kodein by closestKodein()
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: View

   private val recyclerViewItems = state<List<TimelineRecyclerViewItem>>(emptyList())

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      launch {
         val statusIds = action.modules[MASTODON].fetchHomeTimeline()
         recyclerViewItems.value = statusIds.map { StatusItem(it) }
      }
   }

   private fun buildContentView() {
      @OptIn(ExperimentalContracts::class)
      koshian(this) {
         componentView = RecyclerView {
            val adapter = TimelineRecyclerViewAdapter(state, getter)
            vBind(adapter).items.invoke(recyclerViewItems)
            view.layoutManager = LinearLayoutManager(context)
         }
      }

      setContentView(componentView)
   }
}
