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

import androidx.test.ext.junit.runners.*
import androidx.test.rule.*
import org.junit.*
import org.junit.runner.*
import org.koin.core.qualifier.*
import org.koin.dsl.*
import org.koin.test.*
import kotlin.test.*
import kotlin.test.Test

import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.mastodon.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Status as IStatus
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.statuses.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.timelines.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import vue.*
import vue.vuex.preference.*
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimelineTest : KoinTest {
   @get:Rule
   val activityRule = ActivityTestRule(
      TimelineActivity::class.java,
      /* initialTouchMode = */ false,
      /* launchActivity = */ false)

   @Test fun fetchFirstTime() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> = createIStatuses(0)
         })

         launchActivity()

         assertEquals(
            listOf(
               StatusItem(Status.Id("0"))
            ),
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchNewer() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (sinceId == null) {
                  createIStatuses(0)
               } else {
                  createIStatuses(1)
               }
            }
         })

         launchActivity()
         requestFetchNewer()

         assertEquals(
            listOf(
               StatusItem(Status.Id("1")),
               StatusItem(Status.Id("0"))
            ),
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchNewer_missing() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (sinceId == null) {
                  createIStatuses(0)
               } else {
                  createIStatuses(1 until 21)
               }
            }
         })

         launchActivity()
         requestFetchNewer()

         assertEquals(
            listOf(
               *(20 downTo 1).map { StatusItem(Status.Id(it.toString())) }.toTypedArray(),
               MissingStatusItem,
               StatusItem(Status.Id("0"))
            ),
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchOlder() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (maxId == null) {
                  createIStatuses(1 until 21)
               } else {
                  createIStatuses(0)
               }
            }
         })

         launchActivity()
         requestFetchOlder()

         assertEquals(
            (20 downTo 0).map { StatusItem(Status.Id(it.toString())) },
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchOlder_indicator() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (maxId == null) {
                  createIStatuses(1 until 21)
               } else {
                  delay(3000L)
                  createIStatuses(0)
               }
            }
         })

         launchActivity()
         requestFetchOlder()

         assertTrue(
            activityRule.activity.recyclerViewItems().lastOrNull() is LoadingIndicatorItem
         )
      }
   }

   @Test fun fetchOlder_ignoreSecondTime() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            private var invocationCount = 0

            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               val statuses = when (invocationCount) {
                  0 -> createIStatuses(1 until 21)

                  1 -> {
                     delay(3000L)
                     createIStatuses(0)
                  }

                  else -> fail("fetchOlder should ignore on the second time")
               }

               invocationCount++

               return statuses
            }
         })

         launchActivity()
         requestFetchOlder()
         requestFetchOlder()
      }
   }

   @Test fun canFetchOlder_firstTime() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> = createIStatuses(0 until 20)
         })

         launchActivity()

         assertTrue(activityRule.activity.canFetchOlder())
      }
   }

   @Test fun cannotFetchOlder_firstTime() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> = createIStatuses(0)
         })

         launchActivity()

         assertFalse(activityRule.activity.canFetchOlder())
      }
   }

   @Test fun canFetchOlder_afterFetchingOlder() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (maxId == null) {
                  createIStatuses(20 until 40)
               } else {
                  createIStatuses( 0 until 20)
               }
            }
         })

         launchActivity()
         requestFetchOlder()

         assertTrue(activityRule.activity.canFetchOlder())
      }
   }

   @Test fun cannotFetchOlder_afterFetchingOlder() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               return if (maxId == null) {
                  createIStatuses(1 until 21)
               } else {
                  createIStatuses(0)
               }
            }
         })

         launchActivity()
         requestFetchOlder()

         assertFalse(activityRule.activity.canFetchOlder())
      }
   }

   @Test fun fetchMissing() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            private var invocationCount = 0

            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               val statuses = when (invocationCount) {
                  0 -> createIStatuses( 0 until 20)
                  1 -> createIStatuses(21 until 41)
                  2 -> createIStatuses(20)
                  else -> emptyList()
               }

               invocationCount++

               return statuses
            }
         })

         launchActivity()
         requestFetchNewer()

         val missingItemPosition = activityRule.activity.recyclerViewItems()
            .indexOfFirst { it is MissingStatusItem }

         requestFetchMissing(missingItemPosition)

         assertEquals(
            (40 downTo 0).map { StatusItem(Status.Id(it.toString())) },
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchMissing_moreMissing() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            private var invocationCount = 0

            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               val statuses = when (invocationCount) {
                  0 -> createIStatuses( 0 until 20)
                  1 -> createIStatuses(40 until 60)
                  2 -> createIStatuses(20 until 40)
                  else -> emptyList()
               }

               invocationCount++

               return statuses
            }
         })

         launchActivity()
         requestFetchNewer()

         val missingItemPosition = activityRule.activity.recyclerViewItems()
            .indexOfFirst { it is MissingStatusItem }

         requestFetchMissing(missingItemPosition)

         assertEquals(
            listOf(
               *(59 downTo 20).map { StatusItem(Status.Id(it.toString())) }.toTypedArray(),
               MissingStatusItem,
               *(19 downTo  0).map { StatusItem(Status.Id(it.toString())) }.toTypedArray()
            ),
            activityRule.activity.recyclerViewItems()
         )
      }
   }

   @Test fun fetchMissing_indicator() {
      runBlocking {
         startMockedTimelineModule(object : TimelineService {
            private var invocationCount = 0

            override suspend fun fetchHomeTimeline(
               local: Boolean?, onlyMedia: Boolean?,
               maxId: String?, sinceId: String?, limit: Int?
            ): List<IStatus> {
               val statuses = when (invocationCount) {
                  0 -> createIStatuses( 0 until 20)
                  1 -> createIStatuses(40 until 60)

                  else -> {
                     delay(3000L)
                     createIStatuses(20 until 40)
                  }
               }

               invocationCount++

               return statuses
            }
         })

         launchActivity()
         requestFetchNewer()

         val missingItemPosition = activityRule.activity.recyclerViewItems()
            .indexOfFirst { it is MissingStatusItem }

         requestFetchMissing(missingItemPosition)

         assertTrue(
            activityRule.activity.recyclerViewItems()
               .count { it is LoadingIndicatorItem } == 1
         )
      }
   }

   private fun startMockedTimelineModule(timelineService: TimelineService) {
      Application.mastodonModule = module {
         single { TimeZone.getDefault() }

         factory<StatusService> { (credential: Credential) ->
            StatusServiceImpl(
               credential.instanceUrl.toExternalForm(),
               credential.accessToken)
         }

         factory { (_: Credential) -> timelineService }

         factory {
            @OptIn(KtorExperimentalAPI::class, UnstableDefault::class)
            HttpClient(Android) {
               install(JsonFeature) {
                  val jsonConfiguration = JsonConfiguration(ignoreUnknownKeys = true)
                  serializer = KotlinxSerializer(Json(jsonConfiguration))
               }

               ContentEncoding()

               defaultRequest {
                  accept(ContentType.Application.Json)
               }
            }
         }
      }

      Application.applicationModule = module {
         single { Store() }

         factory(named("fetchingTimelineStatusCountLimit")) { 20 }

         single<PreferenceState<String?>>(named("instanceUrlPreference")) {
            StubPreferenceState { "https://example.com" }
         }

         single<PreferenceState<String?>>(named("accessTokenPreference")) {
            StubPreferenceState { "accessToken" }
         }
      }
   }

   private fun createIStatus(id: Int)
         = iStatus(id.toString(), iAccount("0", "wcaokaze"), "content$id")

   private fun createIStatuses(range: IntRange)
         = range.map { createIStatus(it) } .reversed()

   private fun createIStatuses(vararg ids: Int)
         = ids.map { createIStatus(it) }

   private suspend fun launchActivity() {
      activityRule.launchActivity(null)
      delay(50L)
   }

   private suspend fun requestFetchNewer() {
      withContext(Dispatchers.Main) {
         activityRule.activity.fetchNewer()
      }

      delay(50L) // wait for the fetching coroutine
   }

   private suspend fun requestFetchMissing(position: Int) {
      withContext(Dispatchers.Main) {
         activityRule.activity.fetchMissing(position)
      }

      delay(50L) // wait for the fetching coroutine
   }

   private suspend fun requestFetchOlder() {
      withContext(Dispatchers.Main) {
         activityRule.activity.fetchOlder()
      }

      delay(50L) // wait for the fetching coroutine
   }
}
