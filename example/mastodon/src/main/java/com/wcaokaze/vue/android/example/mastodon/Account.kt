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

package com.wcaokaze.vue.android.example.mastodon

import java.io.*
import java.net.*

data class Account(
   val id: Id,
   val name: String,
   val acct: String,
   val isLocked: Boolean,
   val iconUrl: URL?,
   val followerCount: Long,
   val followeeCount: Long,
   val statusCount: Long,
   val biography: String?,
   val fields: List<Pair<String, String>>
) {
   data class Id(val id: String) : Serializable
}
