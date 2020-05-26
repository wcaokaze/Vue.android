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

package com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.timelines

import com.wcaokaze.vue.android.example.mastodon.infrastructure.*
import io.ktor.client.request.*

internal interface TimelineService {
   suspend fun fetchHomeTimeline(
      local: Boolean? = null,
      onlyMedia: Boolean? = null,
      maxId: String? = null,
      sinceId: String? = null,
      limit: Int? = null
   ): List<Status>
}

internal class TimelineServiceImpl(instanceUrl: String, accessToken: String)
   : ApiServiceBase(instanceUrl, accessToken), TimelineService
{
   override suspend fun fetchHomeTimeline(
      local: Boolean?,
      onlyMedia: Boolean?,
      maxId: String?,
      sinceId: String?,
      limit: Int?
   ): List<Status> {
      return get("api/v1/timelines/home") {
         parameter("local", local)
         parameter("only_media", onlyMedia)
         parameter("max_id", maxId)
         parameter("since_id", sinceId)
         parameter("limit", limit)
      }
   }
}
