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

package com.wcaokaze.vue.android.example.mastodon.account

import vue.*
import vue.vuex.*

class AccountStore : VuexStore<AccountState, AccountMutation, AccountAction, AccountGetter>() {
   override fun createState()    = AccountState()
   override fun createMutation() = AccountMutation()
   override fun createAction()   = AccountAction()
   override fun createGetter()   = AccountGetter()
}

class AccountState : VuexState() {
   val accounts = state<Map<Account.Id, Account>>(emptyMap())
}

class AccountMutation : VuexMutation<AccountState>()

class AccountAction : VuexAction<AccountState, AccountMutation, AccountGetter>()

class AccountGetter : VuexGetter<AccountState>() {
   fun getAccount(id: Account.Id): ReactiveField<Account?> = getter {
      state.accounts()[id]
   }
}
