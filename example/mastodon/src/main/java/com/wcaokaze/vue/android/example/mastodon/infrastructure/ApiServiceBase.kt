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

package com.wcaokaze.vue.android.example.mastodon.infrastructure

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.koin.core.*

internal abstract class ApiServiceBase(
   instanceUrl: String,
   protected val accessToken: String
) : KoinComponent {
   private val instanceUrl = if (instanceUrl.endsWith('/')) {
      instanceUrl
   } else {
      "$instanceUrl/"
   }

   protected val httpClient: HttpClient by inject()

   protected fun getApiUrl(path: String): String = instanceUrl + path

   protected suspend inline fun <reified T> get(
      path: String,
      withAuthorizationHeader: Boolean = true,
      block: HttpRequestBuilder.() -> Unit = {}
   ): T {
      httpClient.use { client ->
         return client.get(getApiUrl(path)) {
            if (withAuthorizationHeader) {
               header("Authorization", "Bearer $accessToken")
            }

            block()
         }
      }
   }

   protected suspend inline fun <reified T> post(
      path: String,
      withAuthorizationHeader: Boolean = true,
      block: HttpRequestBuilder.() -> Unit = {}
   ): T {
      httpClient.use { client ->
         return client.request {
            method = HttpMethod.Post
            url(getApiUrl(path))

            if (withAuthorizationHeader) {
               header("Authorization", "Bearer $accessToken")
            }

            block()
         }
      }
   }
}

internal fun HttpRequestBuilder.postParameter(
   parameterBuilder: ParametersBuilder.() -> Unit
) {
   val parameters = Parameters.build(parameterBuilder)
   body = FormDataContent(parameters)
}
