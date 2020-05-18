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

package com.wcaokaze.vue.android.example

import android.content.*
import com.wcaokaze.vue.android.example.mastodon.*
import vue.*
import vue.vuex.*
import vue.vuex.preference.*
import java.net.MalformedURLException
import java.net.URL

class CredentialPreferenceStore(private val context: Context)
      : VuexStore<
            CredentialPreferenceState,
            CredentialPreferenceMutation,
            CredentialPreferenceAction,
            CredentialPreferenceGetter>()
{
   override fun createState()    = CredentialPreferenceState(context)
   override fun createMutation() = CredentialPreferenceMutation()
   override fun createAction()   = CredentialPreferenceAction()
   override fun createGetter()   = CredentialPreferenceGetter()
}

class CredentialPreferenceState(context: Context) : VuexState() {
   private val file = PreferenceFile("Credential")

   val instanceUrl by nullableStringPreferenceState(context, file, default = null)
   val accessToken by nullableStringPreferenceState(context, file, default = null)
}

class CredentialPreferenceMutation : VuexMutation<CredentialPreferenceState>() {
   fun setCredential(credential: Credential) {
      state.instanceUrl.value = credential.instanceUrl.toExternalForm()
      state.accessToken.value = credential.accessToken
   }
}

class CredentialPreferenceAction : VuexAction<CredentialPreferenceState, CredentialPreferenceMutation, CredentialPreferenceGetter>()

class CredentialPreferenceGetter : VuexGetter<CredentialPreferenceState>() {
   val credential = getter {
      val instanceUrl = state.instanceUrl() ?: return@getter null
      val accessToken = state.accessToken() ?: return@getter null

      try {
         Credential(URL(instanceUrl), accessToken)
      } catch (e: MalformedURLException) {
         null
      }
   }
}
