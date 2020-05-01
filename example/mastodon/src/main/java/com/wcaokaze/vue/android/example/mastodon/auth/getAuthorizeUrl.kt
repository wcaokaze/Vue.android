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
import com.wcaokaze.vue.android.example.mastodon.infrastructure.oauth.getAuthorizeUrl
import java.net.*

fun Mastodon.getAuthorizationUrl(client: Client): URL {
   val urlStr = getMastodonInstance(client.instanceUrl.toExternalForm())
         .getAuthorizeUrl(
               client.clientId,
               responseType = "code",
               redirectUri = client.redirectUri,
               scopes = listOf("read", "write", "follow")
         )

   return URL(urlStr)
}
