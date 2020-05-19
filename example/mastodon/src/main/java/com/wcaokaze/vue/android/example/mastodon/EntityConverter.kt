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
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Account as IAccount
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Status as IStatus
import java.net.*
import java.text.*
import java.util.*
import kotlin.collections.*
import kotlin.math.*

internal class EntityConverter(private val timeZone: TimeZone) {
   private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

   private val accountMemo = HashMap<String, Account>()
   private val statusMemo  = HashMap<String, Status>()

   fun getAllConvertedAccounts(): Collection<Account> = accountMemo.values
   fun getAllConvertedStatuses(): Collection<Status>  = statusMemo .values

   fun convertAccount(iAccount: IAccount): Account {
      val account = Account(
         Account.Id(iAccount.id),
         if (iAccount.displayName.isNullOrEmpty()) {
            iAccount.username
         } else {
            iAccount.displayName
         },
         iAccount.acct ?: iAccount.username,
         iAccount.locked ?: false,
         try {
            URL(iAccount.avatarStatic)
         } catch (e: MalformedURLException) {
            null
         },
         iAccount.followersCount ?: 0L,
         iAccount.followingCount ?: 0L,
         iAccount.statusesCount ?: 0L,
         iAccount.note,
         iAccount.fields?.map { Pair(it.name ?: "", it.value ?: "") } ?: emptyList()
      )

      accountMemo[iAccount.id] = account

      return account
   }

   fun convertStatus(iStatus: IStatus): Status {
      if (iStatus.reblog == null) {
         val tooter = convertAccount(iStatus.account)

         val toot = Status.Toot(
            Status.Id(iStatus.id),
            tooter.id,
            iStatus.spoilerText,
            iStatus.content,
            parseDateOrThrow(iStatus.createdAt),
            iStatus.reblogsCount    ?: 0L,
            iStatus.favouritesCount ?: 0L,
            iStatus.reblogged  ?: false,
            iStatus.favourited ?: false
         )

         statusMemo[iStatus.id] = toot

         return toot
      } else {
         val booster = convertAccount(iStatus.account)
         val boostedStatus = convertStatus(iStatus.reblog)

         val toot = when (boostedStatus) {
            is Status.Toot -> boostedStatus
            is Status.Boost -> boostedStatus.toot
         }

         val boost = Status.Boost(
            Status.Id(iStatus.id),
            booster.id,
            toot.copy(
               boostCount    = max(toot.boostCount,    iStatus.reblogsCount    ?: 0L),
               favoriteCount = max(toot.favoriteCount, iStatus.favouritesCount ?: 0L),
               isBoosted   = toot.isBoosted   || iStatus.reblogged  ?: false,
               isFavorited = toot.isFavorited || iStatus.favourited ?: false
            ),
            parseDateOrThrow(iStatus.createdAt)
         )

         statusMemo[iStatus.id] = boost

         return boost
      }
   }

   private fun parseDateOrThrow(dateString: String): Date {
      try {
         val utc = apiDateFormat.parse(dateString)
         return Date(utc.time + timeZone.rawOffset)
      } catch (e: ParseException) {
         throw IOException("can not parse date: $dateString")
      }
   }
}
