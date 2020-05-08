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

package com.wcaokaze.vue.android.example.preference

import android.content.*
import com.wcaokaze.vue.android.example.mastodon.*
import java.net.*

class CredentialPreference(private val context: Context) {
   companion object {
      private const val PREFERENCE_NAME = "c6j5Cu1vnRjyy27y"
      private const val KEY_INSTANCE_URL = "gVsS7XkgCI7BUTbR"
      private const val KEY_ACCESS_TOKEN = "869wVTILehKaENcf"
   }

   var credential: Credential?
      get() {
         val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
         val instanceUrl = preference.getString(KEY_INSTANCE_URL, null) ?: return null
         val accessToken = preference.getString(KEY_ACCESS_TOKEN, null) ?: return null

         return try {
            Credential(URL(instanceUrl), accessToken)
         } catch (e: MalformedURLException) {
            null
         }
      }
      set(value) {
         context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_INSTANCE_URL, value?.instanceUrl?.toExternalForm())
            .putString(KEY_ACCESS_TOKEN, value?.accessToken)
            .apply()
      }
}
