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
import android.widget.*
import androidx.recyclerview.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import koshian.*
import kotlinx.coroutines.*
import org.kodein.di.*
import org.kodein.di.android.*
import vue.*
import vue.koshian.recyclerview.*
import vue.koshian.*
import kotlin.contracts.*

class TimelineActivity : Activity(), VComponentInterface, KodeinAware {
   override val kodein by closestKodein()
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: FrameLayout

   private val recyclerViewItems = state<List<TimelineRecyclerViewItem>>(emptyList())

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      launch {
         val statusIds = try {
            action.modules[MASTODON].fetchHomeTimeline()
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            throw CancellationException()
         }

         recyclerViewItems.value = statusIds.map { StatusItem(it) }
      }
   }

   private fun buildContentView() {
      val recyclerViewAdapter: TimelineRecyclerViewAdapter

      @OptIn(ExperimentalContracts::class)
      koshian(this) {
         componentView = FrameLayout {
            recyclerViewAdapter = Component(TimelineRecyclerViewAdapter(context, state, getter)) {
               component.itemsBinder(recyclerViewItems)
            }
         }
      }

      componentView.applyKoshian {
         Component(recyclerViewAdapter) {
            layout.width  = MATCH_PARENT
            layout.height = MATCH_PARENT
            val layoutManager = LinearLayoutManager(view.context)
            view.layoutManager = layoutManager
            view.addItemDecoration(DividerItemDecoration(view.context, layoutManager.orientation))
         }
      }

      setContentView(componentView)
   }
}
