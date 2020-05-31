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
 *
 */

package com.wcaokaze.vue.android.example

import android.app.Application
import androidx.annotation.*
import com.wcaokaze.vue.android.example.mastodon.*
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
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*
import org.koin.core.module.*
import org.koin.core.qualifier.*
import org.koin.dsl.*
import vue.vuex.preference.*
import java.util.*

class Application : Application() {
   companion object {
      private fun getKoin() = KoinContextHandler.get()

      private var _mastodonModule = module {
         single { TimeZone.getDefault() }

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

      internal var mastodonModule: Module
         get() = _mastodonModule
         @VisibleForTesting set(value) {
            getKoin().unloadModules(listOf(_mastodonModule))
            _mastodonModule = value
            getKoin().loadModules(listOf(value))
         }

      private var _applicationModule = module {
         single { Store() }

         factory(named("fetchingTimelineStatusCountLimit")) { 20 }

         single { PreferenceFile("Credential") }

         single(named("instanceUrlPreference")) {
            PreferenceState(NullableStringPreferenceLoader,
               context = get(),
               file = get(),
               key = "instanceUrl",
               default = null
            )
         }

         single(named("accessTokenPreference")) {
            PreferenceState(NullableStringPreferenceLoader,
               context = get(),
               file = get(),
               key = "accessToken",
               default = null
            )
         }
      }

      internal var applicationModule: Module
         get() = _applicationModule
         @VisibleForTesting set(value) {
            getKoin().unloadModules(listOf(_applicationModule))
            _applicationModule = value
            getKoin().loadModules(listOf(value))
         }
   }

   override fun onCreate() {
      super.onCreate()

      startKoin {
         androidLogger()
         androidContext(this@Application)

         modules(
            mastodonModule,
            applicationModule
         )
      }
   }
}
