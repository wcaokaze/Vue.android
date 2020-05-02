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

package com.wcaokaze.vue.android.example.mastodon

import com.wcaokaze.vue.android.example.mastodon.account.*
import com.wcaokaze.vue.android.example.mastodon.status.*
import vue.vuex.*

class ApiStore : VuexStore<ApiState, ApiMutation, ApiAction, ApiGetter>() {
   object ModuleKeys {
      val ACCOUNT = Module.Key<AccountState,  AccountMutation,  AccountAction,  AccountGetter>()
      val STATUS  = Module.Key<StatusState,   StatusMutation,   StatusAction,   StatusGetter>()
   }

   override fun createState()    = ApiState()
   override fun createMutation() = ApiMutation()
   override fun createAction()   = ApiAction()
   override fun createGetter()   = ApiGetter()

   override fun createModules() = listOf(
         Module(ModuleKeys.ACCOUNT,  AccountStore()),
         Module(ModuleKeys.STATUS,   StatusStore())
   )
}

class ApiState : VuexState()

class ApiMutation : VuexMutation<ApiState>()

class ApiAction : VuexAction<ApiState, ApiMutation, ApiGetter>()

class ApiGetter : VuexGetter<ApiState>()
