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
internal data class Status(
   val id: String,
   val uri: String,
   val account: Account,
   val reblog: Status? = null,
   val content: String,
   @SerialName("created_at") val createdAt: String,
   val emojis: List<Emoji>? = null,
   @SerialName("reblogs_count") val reblogsCount: Long? = null,
   @SerialName("favourites_count") val favouritesCount: Long? = null,
   val reblogged: Boolean? = null,
   val favourited: Boolean? = null,
   val muted: Boolean? = null,
   val sensitive: Boolean? = null,
   @SerialName("spoiler_text") val spoilerText: String? = null,
   @SerialName("media_attachments") val mediaAttachments: List<Attachment>? = null
)
