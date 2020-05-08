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

package com.wcaokaze.vue.android.example.mastodon.auth

import com.wcaokaze.vue.android.example.mastodon.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.oauth.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.apps.*
import org.kodein.di.*
import java.net.*

class MastodonAuthorizator(override val kodein: Kodein) : KodeinAware {
   private fun getMastodonInstance(rootUrl: URL)
         = MastodonInstance(kodein, rootUrl.toExternalForm())

   suspend fun registerClient(instanceUrl: URL, redirectUri: String): Client {
      val mastodonClient = getMastodonInstance(instanceUrl)
         .postApp(
            clientName = "Vue.android-example",
            redirectUris = redirectUri,
            scopes = listOf("read", "write", "follow"),
            website = "https://github.com/wcaokaze/Vue.android"
         )

      return Client(
         instanceUrl,
         mastodonClient.redirectUri,
         mastodonClient.clientId,
         mastodonClient.clientSecret
      )
   }

   fun getAuthorizationUrl(client: Client): URL {
      val urlStr = getMastodonInstance(client.instanceUrl)
         .getAuthorizeUrl(
            client.clientId,
            responseType = "code",
            redirectUri = client.redirectUri,
            scopes = listOf("read", "write", "follow")
         )

      return URL(urlStr)
   }

   suspend fun publishCredential(client: Client, authCode: String): Credential {
      val token = getMastodonInstance(client.instanceUrl)
         .getToken(
            client.clientId,
            client.clientSecret,
            client.redirectUri,
            /* grant_type = */ "authorization_code",
            authCode
         )

      return Credential(
         client.instanceUrl,
         token.accessToken
      )
   }
}