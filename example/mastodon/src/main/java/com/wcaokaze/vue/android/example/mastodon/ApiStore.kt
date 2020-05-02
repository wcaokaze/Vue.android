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

import vue.*
import vue.vuex.*

class ApiStore : VuexStore<ApiState, ApiMutation, ApiAction, ApiGetter>() {
   override fun createState()    = ApiState()
   override fun createMutation() = ApiMutation()
   override fun createAction()   = ApiAction()
   override fun createGetter()   = ApiGetter()
}

class ApiState : VuexState() {
   val accounts = state<Map<Account.Id, Account>>(emptyMap())
   val statuses = state<Map<Status .Id, Status>> (emptyMap())
}

class ApiMutation : VuexMutation<ApiState>()

class ApiAction : VuexAction<ApiState, ApiMutation, ApiGetter>()

class ApiGetter : VuexGetter<ApiState>() {
   fun getAccount(id: Account.Id): ReactiveField<Account?>
         = getter { state.accounts()[id] }

   fun getStatus(id: Status.Id): ReactiveField<Status?>
         = getter { state.statuses()[id] }
}
