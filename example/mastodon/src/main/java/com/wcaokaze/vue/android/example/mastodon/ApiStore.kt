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
