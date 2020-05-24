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

import com.wcaokaze.vue.android.example.mastodon.*
import vue.vuex.*

class Store : VuexStore<State, Mutation, Action, Getter>() {
   object ModuleKeys {
      val MASTODON = Module.Key<MastodonStore, MastodonState, MastodonMutation, MastodonAction, MastodonGetter>()
      val CREDENTIAL_PREFERENCE = Module.Key<CredentialPreferenceStore, CredentialPreferenceState, CredentialPreferenceMutation, CredentialPreferenceAction, CredentialPreferenceGetter>()
   }

   override fun createState()    = State()
   override fun createMutation() = Mutation()
   override fun createAction()   = Action()
   override fun createGetter()   = Getter()

   override fun createModules() = listOf(
      Module(ModuleKeys.MASTODON, MastodonStore()),
      Module(ModuleKeys.CREDENTIAL_PREFERENCE, CredentialPreferenceStore())
   )
}

class State : VuexState()
class Mutation : VuexMutation<State>()
class Action : VuexAction<State, Mutation, Getter>()
class Getter : VuexGetter<State>()
