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

package com.wcaokaze.vue.android.example.mastodon

import org.junit.runner.*
import org.junit.runners.*
import org.koin.core.context.*
import org.koin.dsl.*
import org.koin.test.*
import kotlin.test.*

import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.statuses.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.timelines.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import vue.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.*
import java.net.*
import java.util.*

@RunWith(JUnit4::class)
class HomeTimelineTest : KoinTest {
   @AfterTest fun stop() {
      stopKoin()
   }

   @Test fun failsIfNoCredential() {
      runBlocking {
         startMockJsonKoin { "[]" }

         val store = MastodonStore()

         assertFails {
            store.action.fetchHomeTimeline()
         }
      }
   }

   @Test fun ioExceptionCanBeCaught() {
      runBlocking {
         startMockJsonKoin {
            throw IOException("Exception caused by network")
         }

         val store = MastodonStore()

         val exception = assertFailsWith<IOException> {
            store.mutation.setCredential(
               Credential(URL("https://example.com"), "0123456789abcdef"))

            store.action.fetchHomeTimeline()
         }

         val message = exception.message
         assertNotNull(message)
         assertEquals("Exception caused by network", message)
      }
   }

   @Test fun returnsStatusIds() {
      runBlocking {
         startMockJsonKoin {
            """
               [
                  {
                     "id": "0",
                     "account": {
                        "id": "0",
                        "username": "Account 0"
                     },
                     "content": "Toot 0",
                     "created_at": "2000-01-01T00:00:00.000Z"
                  },
                  {
                     "id": "1",
                     "account": {
                        "id": "0",
                        "username": "Account 0"
                     },
                     "content": "Toot 1",
                     "created_at": "2000-01-01T00:00:00.000Z"
                  }
               ]
            """
         }

         val store = MastodonStore()

         store.mutation.setCredential(
            Credential(URL("https://example.com"), "0123456789abcdef"))

         val statusIds = store.action.fetchHomeTimeline()
         val expectedIds = listOf(Status.Id("0"), Status.Id("1"))
         assertEquals(expectedIds, statusIds)
      }
   }

   @Test fun entitiesIsStored() {
      runBlocking {
         startMockJsonKoin {
            """
               [
                  {
                     "id": "0",
                     "account": {
                        "id": "0",
                        "username": "Account 0"
                     },
                     "content": "Toot 0",
                     "created_at": "2000-01-01T00:00:00.000Z"
                  },
                  {
                     "id": "1",
                     "account": {
                        "id": "0",
                        "username": "Account 0"
                     },
                     "content": "Toot 1",
                     "created_at": "2000-01-01T00:00:00.000Z"
                  },
                  {
                     "id": "3",
                     "account": {
                        "id": "2",
                        "username": "Account 2"
                     },
                     "content": "Toot 3",
                     "created_at": "2000-01-01T00:00:00.000Z"
                  },
                  {
                     "id": "5",
                     "account": {
                        "id": "5",
                        "username": "Account 5"
                     },
                     "content": "toot 4",
                     "created_at": "2000-01-01T00:00:00.000Z",
                     "reblog": {
                        "id": "4",
                        "account": {
                           "id": "4",
                           "username": "Account 4"
                        },
                        "content": "Toot 4",
                        "created_at": "2000-01-01T00:00:00.000Z"
                     }
                  }
               ]
            """
         }

         val store = MastodonStore()

         store.mutation.setCredential(
            Credential(URL("https://example.com"), "0123456789abcdef"))

         store.action.fetchHomeTimeline()

         val expectedAccounts = mapOf(
            Account.Id("0") to account(id = "0", name = "Account 0"),
            Account.Id("2") to account(id = "2", name = "Account 2"),
            Account.Id("4") to account(id = "4", name = "Account 4"),
            Account.Id("5") to account(id = "5", name = "Account 5")
         )

         assertEquals(expectedAccounts, store.state.accounts.value)

         val expectedStatuses = mapOf(
            Status.Id("0") to toot(id = "0", tooterAccountId = "0", content = "Toot 0"),
            Status.Id("1") to toot(id = "1", tooterAccountId = "0", content = "Toot 1"),
            Status.Id("3") to toot(id = "3", tooterAccountId = "2", content = "Toot 3"),
            Status.Id("4") to toot(id = "4", tooterAccountId = "4", content = "Toot 4"),
            Status.Id("5") to boost(id = "5", boosterAccountId = "5",
               toot = toot(id = "4", tooterAccountId = "4", content = "Toot 4"))
         )

         assertEquals(expectedStatuses, store.state.statuses.value)
      }
   }

   private fun account(
      id: String,
      name: String,
      acct: String = name,
      isLocked: Boolean = false,
      iconUrl: URL? = null,
      followerCount: Long = 0L,
      followeeCount: Long = 0L,
      statusCount: Long = 0L,
      biography: String? = null,
      fields: List<Pair<String, String>> = emptyList()
   ) = Account(
      Account.Id(id),
      name,
      acct,
      isLocked,
      iconUrl,
      followerCount,
      followeeCount,
      statusCount,
      biography,
      fields
   )

   private fun toot(
      id: String,
      tooterAccountId: String,
      spoilerText: String? = null,
      content: String = "Toot $id",
      tootedDate: Date = @Suppress("DEPRECATION") Date(100, 0, 1),
      boostCount: Long = 0L,
      favoriteCount: Long = 0L,
      isBoosted: Boolean = false,
      isFavorited: Boolean = false
   ) = Status.Toot(
      Status.Id(id),
      Account.Id(tooterAccountId),
      spoilerText,
      content,
      tootedDate,
      boostCount,
      favoriteCount,
      isBoosted,
      isFavorited
   )

   private fun boost(
      id: String,
      boosterAccountId: String,
      toot: Status.Toot,
      boostedDate: Date = @Suppress("DEPRECATION") Date(100, 0, 1)
   ) = Status.Boost(
      Status.Id(id),
      Account.Id(boosterAccountId),
      toot,
      boostedDate
   )

   private fun startMockJsonKoin(mockJson: suspend (HttpRequestData) -> String) {
      val module = module {
         single { TimeZone.getTimeZone("UTC") }

         factory<StatusService> { (credential: Credential) ->
            StatusServiceImpl(
               credential.instanceUrl.toExternalForm(),
               credential.accessToken)
         }

         factory<TimelineService> { (credential: Credential) ->
            TimelineServiceImpl(
               credential.instanceUrl.toExternalForm(),
               credential.accessToken)
         }

         factory {
            @OptIn(UnstableDefault::class)
            HttpClient(MockEngine) {
               install(JsonFeature) {
                  val jsonConfiguration = JsonConfiguration(ignoreUnknownKeys = true)
                  serializer = KotlinxSerializer(Json(jsonConfiguration))
               }

               defaultRequest {
                  accept(ContentType.Application.Json)
               }

               engine {
                  addHandler { request ->
                     val json = mockJson(request)

                     respond(json,
                        headers = headersOf(
                           "Content-Type" to listOf(ContentType.Application.Json.toString())
                        )
                     )
                  }
               }
            }
         }
      }

      startKoin {
         modules(module)
      }
   }
}
