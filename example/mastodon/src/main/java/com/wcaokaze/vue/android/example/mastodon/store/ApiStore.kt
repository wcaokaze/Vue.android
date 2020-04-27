package com.wcaokaze.vue.android.example.mastodon.store

import com.wcaokaze.vue.android.example.mastodon.store.account.*
import com.wcaokaze.vue.android.example.mastodon.store.status.*
import com.wcaokaze.vue.android.example.mastodon.store.timeline.*
import vue.vuex.*

class ApiStore : VuexStore<ApiState, ApiMutation, ApiAction, ApiGetter>() {
   object ModuleKeys {
      val ACCOUNT  = Module.Key<AccountState,  AccountMutation,  AccountAction,  AccountGetter>()
      val STATUS   = Module.Key<StatusState,   StatusMutation,   StatusAction,   StatusGetter>()
      val TIMELINE = Module.Key<TimelineState, TimelineMutation, TimelineAction, TimelineGetter>()
   }

   override fun createState()    = ApiState()
   override fun createMutation() = ApiMutation()
   override fun createAction()   = ApiAction()
   override fun createGetter()   = ApiGetter()

   override fun createModules() = listOf(
      Module(ModuleKeys.ACCOUNT,  AccountStore()),
      Module(ModuleKeys.STATUS,   StatusStore()),
      Module(ModuleKeys.TIMELINE, TimelineStore())
   )
}

class ApiState : VuexState()

class ApiMutation : VuexMutation<ApiState>()

class ApiAction : VuexAction<ApiState, ApiMutation, ApiGetter>()

class ApiGetter : VuexGetter<ApiState>()
