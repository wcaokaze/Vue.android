package com.wcaokaze.vue.android.example.api.store.status

import vue.*
import vue.vuex.*

class StatusStore : VuexStore<StatusState, StatusMutation, StatusAction, StatusGetter>() {
   override fun createState()    = StatusState()
   override fun createMutation() = StatusMutation()
   override fun createAction()   = StatusAction()
   override fun createGetter()   = StatusGetter()
}

class StatusState : VuexState() {
   val statuses = state<Map<Status.Id, Status>>(emptyMap())
}

class StatusMutation : VuexMutation<StatusState>()

class StatusAction : VuexAction<StatusState, StatusMutation, StatusGetter>()

class StatusGetter : VuexGetter<StatusState>() {
   fun getStatus(id: Status.Id): ReactiveField<Status?> = getter {
      state.statuses()[id]
   }
}
