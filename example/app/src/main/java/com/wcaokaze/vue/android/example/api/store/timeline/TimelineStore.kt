package com.wcaokaze.vue.android.example.api.store.timeline

import com.wcaokaze.vue.android.example.api.store.status.*
import vue.vuex.*

class TimelineStore : VuexStore<TimelineState, TimelineMutation, TimelineAction, TimelineGetter>() {
   override fun createState()    = TimelineState()
   override fun createMutation() = TimelineMutation()
   override fun createAction()   = TimelineAction()
   override fun createGetter()   = TimelineGetter()
}

class TimelineState : VuexState() {
   val statusIds = state<List<Status.Id>>(emptyList())
}

class TimelineMutation : VuexMutation<TimelineState>()

class TimelineAction : VuexAction<TimelineState, TimelineMutation, TimelineGetter>()

class TimelineGetter : VuexGetter<TimelineState>()
