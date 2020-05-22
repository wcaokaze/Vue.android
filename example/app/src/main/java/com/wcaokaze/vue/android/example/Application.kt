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
import org.kodein.di.*
import org.kodein.di.generic.*
import java.util.*

class Application : Application(), KodeinAware {
   override val kodein = Kodein.lazy {
      bind<TimeZone>() with provider { TimeZone.getDefault() }

      bind<HttpClient>() with provider {
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

   val store    by lazy { Store(kodein, this) }
   val state    by lazy { store.state }
   val mutation by lazy { store.mutation }
   val action   by lazy { store.action }
   val getter   by lazy { store.getter }
}
