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
import org.koin.core.qualifier.*
import vue.*
import vue.androidx.*
import vue.koshian.recyclerview.*
import vue.koshian.*
import vue.stream.*

class TimelineActivity : Activity(), VComponentInterface<Store> {
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: FrameLayout
   private lateinit var layoutManager: LinearLayoutManager

   override val store: Store by inject()
   private val fetchingStatusCountLimit: Int by inject(named("fetchingTimelineStatusCountLimit"))

   @VisibleForTesting val recyclerViewItems = state<List<TimelineRecyclerViewItem>>(emptyList())
   @VisibleForTesting val canFetchOlder = state(false)

   private val fetchingNewerJob = state(Job.completed())
   private val fetchingOlderJob = state(Job.completed())
   private val isFetchingNewer = getter { fetchingNewerJob().toReactiveField()() }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      val credential = getter[CREDENTIAL_PREFERENCE].credential()

      if (credential != null) {
         mutation[MASTODON].setCredential(credential)
      }

      launch {
         val statusCountLimit = fetchingStatusCountLimit

         val statusIds = try {
            action[MASTODON].fetchHomeTimeline(statusCountLimit = statusCountLimit)
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            throw CancellationException()
         }

         recyclerViewItems.value = statusIds.map { StatusItem(it) }
         canFetchOlder.value = statusIds.size >= statusCountLimit
      }
   }

   private fun startStatusActivity(statusId: Status.Id) {
      val intent = Intent(this, StatusActivity::class.java)
         .putExtra(StatusActivity.INTENT_KEY_STATUS_ID, statusId)

      startActivity(intent)
   }

   @VisibleForTesting fun fetchNewer() {
      fetchingNewerJob().cancel()

      fetchingNewerJob.value = launch {
         val sinceId = recyclerViewItems()
            .asSequence()
            .filterIsInstance<StatusItem>()
            .firstOrNull()
            ?.statusId
            ?: throw CancellationException()

         val statusCountLimit = fetchingStatusCountLimit

         val fetchedStatuses = try {
            action[MASTODON].fetchHomeTimeline(
               sinceId = sinceId, statusCountLimit = statusCountLimit)
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            Toast.makeText(this@TimelineActivity, "Something goes wrong", Toast.LENGTH_LONG).show()
            throw CancellationException()
         }

         val newerItems = fetchedStatuses.map { StatusItem(it) }

         val olderItems = recyclerViewItems()
            .dropWhile { it is LoadingIndicatorItem || it is MissingStatusItem }

         recyclerViewItems.value = if (newerItems.size < statusCountLimit) {
            newerItems + olderItems
         } else {
            newerItems + MissingStatusItem + olderItems
         }
      }
   }

   @VisibleForTesting fun fetchMissing(position: Int) {
      val beforeFetchingItems = recyclerViewItems()

      if (beforeFetchingItems.getOrNull(position) !is MissingStatusItem) { return }

      // --------

      val newerPartition = beforeFetchingItems
         .subList(0, position)
         .dropLastWhile { it is LoadingIndicatorItem || it is MissingStatusItem }

      val olderPartition = beforeFetchingItems
         .subList(position + 1, beforeFetchingItems.size)
         .dropWhile { it is LoadingIndicatorItem || it is MissingStatusItem }

      val loadingIndicatorPosition = newerPartition.lastIndex + 1
      val loadingIndicatorItem = LoadingIndicatorItem()

      val loadingItems = newerPartition + loadingIndicatorItem + olderPartition
      recyclerViewItems.value = loadingItems

      // --------

      launch {
         val maxId = loadingItems
            .subList(0, loadingIndicatorPosition)
            .asReversed()
            .asSequence()
            .filterIsInstance<StatusItem>()
            .firstOrNull()
            ?.statusId

         val sinceId = loadingItems
            .subList(loadingIndicatorPosition, loadingItems.size)
            .asSequence()
            .filterIsInstance<StatusItem>()
            .firstOrNull()
            ?.statusId

         val statusCountLimit = fetchingStatusCountLimit

         val fetchedStatuses = try {
            action[MASTODON].fetchHomeTimeline(
               maxId = maxId, sinceId = sinceId,
               statusCountLimit = statusCountLimit)
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            Toast.makeText(this@TimelineActivity, "Something goes wrong", Toast.LENGTH_LONG).show()
            throw CancellationException()
         }

         // --------

         val insertingItems = fetchedStatuses.map { StatusItem(it) }

         val afterFetchingItems = recyclerViewItems()
         val insertPosition = afterFetchingItems.indexOf(loadingIndicatorItem)
         val newerItems = afterFetchingItems.subList(0, insertPosition)
         val olderItems = afterFetchingItems.subList(insertPosition + 1, afterFetchingItems.size)

         recyclerViewItems.value = if (fetchedStatuses.size < statusCountLimit) {
            newerItems + insertingItems + olderItems
         } else {
            newerItems + insertingItems + MissingStatusItem + olderItems
         }
      }
   }

   @VisibleForTesting fun fetchOlder() {
      if (fetchingOlderJob().isActive) { return }

      fetchingOlderJob.value = launch {
         recyclerViewItems.value =
            recyclerViewItems().dropLastWhile {
               it is LoadingIndicatorItem || it is MissingStatusItem
            } +
            LoadingIndicatorItem()

         val maxId = recyclerViewItems()
            .asReversed()
            .asSequence()
            .filterIsInstance<StatusItem>()
            .firstOrNull()
            ?.statusId
            ?: throw CancellationException()

         val statusCountLimit = fetchingStatusCountLimit

         val fetchedStatuses = try {
            action[MASTODON].fetchHomeTimeline(
               maxId = maxId, statusCountLimit = statusCountLimit)
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            Toast.makeText(this@TimelineActivity, "Something goes wrong", Toast.LENGTH_LONG).show()
            throw CancellationException()
         }

         val olderItems = fetchedStatuses.map { StatusItem(it) }

         val newerItems = recyclerViewItems()
            .dropLastWhile { it is LoadingIndicatorItem || it is MissingStatusItem }

         recyclerViewItems.value = newerItems + olderItems
         canFetchOlder.value = olderItems.size >= statusCountLimit
      }
   }

   private fun buildContentView() {
      val recyclerViewAdapter: TimelineRecyclerViewAdapter

      koshian(this) {
         componentView = FrameLayout {
            SwipeRefreshLayout {
               vBind.isRefreshing(isFetchingNewer)
               vOn.refresh { fetchNewer() }

               recyclerViewAdapter = Component[::TimelineRecyclerViewAdapter, MASTODON] {
                  component.itemsBinder(recyclerViewItems)

                  component.onItemClick { _, item ->
                     if (item is StatusItem) {
                        startStatusActivity(item.statusId)
                     }
                  }

                  component.onScrolled
                     .map { _, _ -> layoutManager.findLastVisibleItemPosition() }
                     .filter { it >= recyclerViewItems().lastIndex }
                     .filter { canFetchOlder() }
                     .invoke { fetchOlder() }
               }
            }
         }
      }

      componentView.applyKoshian {
         SwipeRefreshLayout {
            Component[recyclerViewAdapter] {
               layout.width  = MATCH_PARENT
               layout.height = MATCH_PARENT
               layoutManager = LinearLayoutManager(view.context)
               view.layoutManager = layoutManager
               view.addItemDecoration(DividerItemDecoration(view.context, layoutManager.orientation))
            }
         }
      }

      setContentView(componentView)
   }
}
