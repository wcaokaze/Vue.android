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
import android.content.*
import android.os.*
import android.widget.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.Store.ModuleKeys.CREDENTIAL_PREFERENCE
import com.wcaokaze.vue.android.example.Store.ModuleKeys.MASTODON
import com.wcaokaze.vue.android.example.activity.status.*
import com.wcaokaze.vue.android.example.mastodon.*
import koshian.*
import koshian.androidx.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.*
import vue.*
import vue.androidx.*
import vue.koshian.recyclerview.*
import vue.koshian.*
import vue.stream.*
import kotlin.contracts.*

class TimelineActivity : Activity(), VComponentInterface<Store> {
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: FrameLayout

   override val store: Store by inject()

   @VisibleForTesting
   val recyclerViewItems = state<List<TimelineRecyclerViewItem>>(emptyList())

   private val fetchingNewerJob = state(Job.completed())
   private val isFetchingNewer = getter { fetchingNewerJob().toReactiveField()() }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      val credential = getter[CREDENTIAL_PREFERENCE].credential()

      if (credential != null) {
         mutation[MASTODON].setCredential(credential)
      }

      launch {
         val statusIds = try {
            action[MASTODON].fetchHomeTimeline()
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            throw CancellationException()
         }

         recyclerViewItems.value = statusIds.map { StatusItem(it) }
      }
   }

   private fun startStatusActivity(statusId: Status.Id) {
      val intent = Intent(this, StatusActivity::class.java)
         .putExtra(StatusActivity.INTENT_KEY_STATUS_ID, statusId)

      startActivity(intent)
   }

   private fun fetchNewer() {
      fetchingNewerJob().cancel()

      fetchingNewerJob.value = launch {
         try {
            val sinceId = recyclerViewItems()
               .filterIsInstance<StatusItem>()
               .firstOrNull()
               ?.statusId
               ?: throw CancellationException()

            val newerIds = action[MASTODON].fetchHomeTimeline(sinceId = sinceId)

            val newerItems = newerIds.map { StatusItem(it) }

            val olderItems = recyclerViewItems()
               .dropWhile { it is LoadingIndicatorItem || it is MissingStatusItem }

            recyclerViewItems.value = newerItems + olderItems
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            Toast.makeText(this@TimelineActivity, "Something goes wrong", Toast.LENGTH_LONG).show()
         }
      }
   }

   private fun buildContentView() {
      val recyclerViewAdapter: TimelineRecyclerViewAdapter

      @OptIn(ExperimentalContracts::class)
      koshian(this) {
         componentView = FrameLayout {
            SwipeRefreshLayout {
               vBind.isRefreshing(isFetchingNewer)
               vOn.refresh { fetchNewer() }

               recyclerViewAdapter = Component[::TimelineRecyclerViewAdapter, MASTODON] {
                  component.itemsBinder(recyclerViewItems)

                  component.onItemClick
                     .map { _, item -> item }
                     .filterIsInstance<StatusItem>()
                     .invoke { startStatusActivity(it.statusId) }
               }
            }
         }
      }

      componentView.applyKoshian {
         SwipeRefreshLayout {
            Component[recyclerViewAdapter] {
               layout.width  = MATCH_PARENT
               layout.height = MATCH_PARENT
               val layoutManager = LinearLayoutManager(view.context)
               view.layoutManager = layoutManager
               view.addItemDecoration(DividerItemDecoration(view.context, layoutManager.orientation))
            }
         }
      }

      setContentView(componentView)
   }
}
