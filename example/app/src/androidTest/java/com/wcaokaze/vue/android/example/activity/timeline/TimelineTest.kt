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
               local: Boolean?,
               onlyMedia: Boolean?,
               maxId: String?,
               sinceId: String?,
               limit: Int?
            ): List<IStatus> {
               return listOf(
                  iStatus(
                     "0",
                     iAccount(
                        "0",
                        "wcaokaze"
                     ),
                     "content"
                  )
               )
            }
         })

         activityRule.launchActivity(null)

         assertEquals(
            listOf(
               StatusItem(Status.Id("0"))
            ),
            activityRule.activity.recyclerViewItems()
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

         single<PreferenceState<String?>>(named("instanceUrlPreference")) {
            StubPreferenceState { "https://example.com" }
         }

         single<PreferenceState<String?>>(named("accessTokenPreference")) {
            StubPreferenceState { "accessToken" }
         }
      }
   }
}
