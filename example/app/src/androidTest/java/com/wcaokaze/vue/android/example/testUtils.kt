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

package com.wcaokaze.vue.android.example

import com.wcaokaze.vue.android.example.mastodon.Account
import com.wcaokaze.vue.android.example.mastodon.Status
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Attachment
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Emoji
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Account as IAccount
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Status as IStatus
import java.net.*
import java.util.*

fun account(
   id: String,
   name: String,
   acct: String = name,
   isLocked: Boolean = false,
   iconUrl: URL? = null,
   followerCount: Long = 0L,
   followeeCount: Long = 0L,
   statusCount: Long = 0L,
   biography: String? = null,
   fields: List<Pair<String, String>> = emptyList()
) = Account(
   Account.Id(id), name, acct, isLocked, iconUrl, followerCount, followeeCount,
   statusCount, biography, fields
)

fun toot(
   id: String,
   tooterAccountId: String,
   spoilerText: String? = null,
   content: String = "Toot $id",
   tootedDate: Date = @Suppress("DEPRECATION") (Date(100, 0, 1)),
   boostCount: Long = 0L,
   favoriteCount: Long = 0L,
   isBoosted: Boolean = false,
   isFavorited: Boolean = false
) = Status.Toot(
   Status.Id(id), Account.Id(tooterAccountId), spoilerText, content, tootedDate,
   boostCount, favoriteCount, isBoosted, isFavorited
)

fun boost(
   id: String,
   boosterAccountId: String,
   toot: Status.Toot,
   boostedDate: Date = @Suppress("DEPRECATION") (Date(100, 0, 1))
) = Status.Boost(
   Status.Id(id), Account.Id(boosterAccountId), toot, boostedDate
)

fun iAccount(
   id: String,
   username: String,
   acct: String? = null,
   displayName: String? = null,
   locked: Boolean? = null,
   followersCount: Long? = null,
   followingCount: Long? = null,
   statusesCount: Long? = null,
   note: String? = null,
   avatarStatic: String? = null,
   headerStatic: String? = null,
   fields: List<IAccount.Field>? = null
) = IAccount(
   id, username, acct, displayName, locked, followersCount, followingCount,
   statusesCount, note, avatarStatic, headerStatic, fields
)

fun iStatus(
   id: String,
   account: IAccount,
   content: String,
   reblog: IStatus? = null,
   createdAt: String = "2000-01-01T00:00:00.000Z",
   emojis: List<Emoji>? = null,
   reblogsCount: Long? = null,
   favouritesCount: Long? = null,
   reblogged: Boolean? = null,
   favourited: Boolean? = null,
   muted: Boolean? = null,
   sensitive: Boolean? = null,
   spoilerText: String? = null,
   mediaAttachments: List<Attachment>? = null
) = IStatus(
   id, account, reblog, content, createdAt, emojis, reblogsCount, favouritesCount,
   reblogged, favourited, muted, sensitive, spoilerText, mediaAttachments
)
