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
import org.koin.dsl.*
import java.util.*

class Application : Application() {
   companion object {
      @set:VisibleForTesting
      internal var mastodonModule = module {
         single { TimeZone.getDefault() }

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

      @set:VisibleForTesting
      internal var applicationModule = module {
         single { Store() }
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
