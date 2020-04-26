package com.wcaokaze.vue.android.example.store.account

import java.net.URL

data class Account(
   val id: Id,
   val name: String,
   val iconUrl: URL,
   val isFollowing: Boolean,
   val isFollowed: Boolean
) {
   class Id(val id: String)
}
