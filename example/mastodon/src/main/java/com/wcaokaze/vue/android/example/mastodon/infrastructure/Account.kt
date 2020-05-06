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
 *
 */

package com.wcaokaze.vue.android.example.mastodon.infrastructure

import kotlinx.serialization.*

@Serializable
internal data class Account(
   val id: String,
   val username: String,
   val acct: String? = null,
   @SerialName("display_name") val displayName: String? = null,
   val locked: Boolean? = null,
   @SerialName("followers_count") val followersCount: Long? = null,
   @SerialName("following_count") val followingCount: Long? = null,
   @SerialName("statuses_count") val statusesCount: Long? = null,
   val note: String? = null,
   @SerialName("avatar_static") val avatarStatic: String? = null,
   @SerialName("header_static") val headerStatic: String? = null,
   val fields: List<Field>? = null
) {
   @Serializable
   internal data class Field(
      val name: String? = null,
      val value: String? = null
   )
}
