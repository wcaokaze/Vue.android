package com.wcaokaze.vue.android.example.mastodon.store.account

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
