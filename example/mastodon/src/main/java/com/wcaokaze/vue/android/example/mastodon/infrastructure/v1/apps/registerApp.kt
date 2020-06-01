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

package com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.apps

import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.koin.core.context.*

internal suspend fun postApp(
   instanceUrl: String,
   clientName: String,
   redirectUris: String,
   scopes: List<String>,
   website: String?
): MastodonClient {
   val httpClient: HttpClient = KoinContextHandler.get().get()
   val urlWithoutSuffix = instanceUrl.removeSuffix("/")

   return httpClient.use {
      it.submitForm(
         "$urlWithoutSuffix/api/v1/apps",
         Parameters.build {
            append("client_name", clientName)
            append("redirect_uris", redirectUris)
            append("scopes", scopes.joinToString(separator = " "))

            if (website != null) {
               append("website", website)
            }
         }
      )
   }
}
