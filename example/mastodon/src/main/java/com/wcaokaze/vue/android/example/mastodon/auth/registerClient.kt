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
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.apps.*
import java.net.*

suspend fun (@Suppress("UNUSED") Mastodon)
      .registerClient(instanceUrl: URL): Client
{
   val mastodonClient = MastodonInstance(instanceUrl.toExternalForm())
         .registerApp(
               clientName = "Vue.android-example",
               redirectUris = BuildConfig.REDIRECT_URI,
               scopes = listOf("read", "write", "follow"),
               website = "https://github.com/wcaokaze/Vue.android"
         )

   return Client(
         instanceUrl,
         mastodonClient.clientId,
         mastodonClient.clientSecret
   )
}
