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

package com.wcaokaze.vue.android.example.mastodon.infrastructure.oauth

import io.ktor.http.*

internal fun getAuthorizeUrl(
   instanceUrl: String,
   clientId: String,
   responseType: String,
   redirectUri: String,
   scopes: List<String>
): String {
   val urlBuilder = URLBuilder(instanceUrl)
   urlBuilder.path("oauth/authorize")
   urlBuilder.parameters.append("client_id", clientId)
   urlBuilder.parameters.append("response_type", responseType)
   urlBuilder.parameters.append("redirect_uri", redirectUri)
   urlBuilder.parameters.append("scope", scopes.joinToString(separator = " "))
   return urlBuilder.buildString()
}
