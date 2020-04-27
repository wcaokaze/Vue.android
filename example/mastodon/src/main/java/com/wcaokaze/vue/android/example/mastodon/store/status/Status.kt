package com.wcaokaze.vue.android.example.mastodon.store.status

import com.wcaokaze.vue.android.example.mastodon.store.account.*
import java.util.*

data class Status(
   val id: Id,
   val accountId: Account.Id,
   val content: String,
   val createdDate: Date,
   val reblogCount: Int,
   val favoriteCount: Int,
   val isReblogged: Boolean,
   val isFavorited: Boolean
) {
   class Id(val id: String)
}
