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
import java.util.*

sealed class Status {
   data class Id(val id: String) : Serializable

   abstract val id: Id

   data class Toot(
      override val id: Id,
      val tooterAccountId: Account.Id,
      val spoilerText: String?,
      val content: String,
      val tootedDate: Date,
      val boostCount: Long,
      val favoriteCount: Long,
      val isBoosted: Boolean,
      val isFavorited: Boolean
   ) : Status()

   data class Boost(
      override val id: Id,
      val boosterAccountId: Account.Id,
      val toot: Toot,
      val boostedDate: Date
   ) : Status()
}
