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

import com.wcaokaze.vue.android.example.mastodon.infrastructure.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Account as IAccount
import com.wcaokaze.vue.android.example.mastodon.infrastructure.Status as IStatus
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.timelines.*
import org.kodein.di.*
import org.kodein.di.generic.*
import vue.*
import vue.vuex.*
import java.io.*
import java.net.*
import java.text.*
import java.util.*
import kotlin.collections.*
import kotlin.math.*

class ApiStore(private val kodein: Kodein)
   : VuexStore<ApiState, ApiMutation, ApiAction, ApiGetter>()
{
   override fun createState()    = ApiState(kodein)
   override fun createMutation() = ApiMutation()
   override fun createAction()   = ApiAction()
   override fun createGetter()   = ApiGetter()
}

// =============================================================================

class ApiState(override val kodein: Kodein) : VuexState(), KodeinAware {
   val timeZone: TimeZone by instance()

   val credential = state<Credential?>(null)

   val accounts = state<Map<Account.Id, Account>>(emptyMap())
   val statuses = state<Map<Status .Id, Status>> (emptyMap())
}

// =============================================================================

class ApiMutation : VuexMutation<ApiState>() {
   fun setCredential(credential: Credential) {
      state.credential.value = credential
      state.accounts.value = emptyMap()
      state.statuses.value = emptyMap()
   }

   fun addAccounts(accounts: Iterable<Account>) {
      state.accounts.value =
         state.accounts.value + accounts.asSequence().map { it.id to it }
   }

   fun addStatuses(statuses: Iterable<Status>) {
      state.statuses.value =
         state.statuses.value + statuses.asSequence().map { it.id to it }
   }
}

// =============================================================================

class ApiAction : VuexAction<ApiState, ApiMutation, ApiGetter>() {
   suspend fun fetchHomeTimeline(
      maxId: Status.Id? = null,
      sinceId: Status.Id? = null
   ): List<Status.Id> {
      val credential = getter.getCredentialOrThrow()

      val iStatuses = getter.getMastodonInstance(credential)
         .getHomeTimeline(
            credential.accessToken,
            maxId = maxId?.id,
            sinceId = sinceId?.id
         )

      val accountMemo = HashMap<String, Account>()
      val statusMemo  = HashMap<String, Status>()

      val statusIds = iStatuses.map { convertStatus(statusMemo, accountMemo, it).id }

      mutation.addAccounts(accountMemo.values)
      mutation.addStatuses(statusMemo .values)

      return statusIds
   }

   private fun convertAccount(
      accountMemo: MutableMap<String, in Account>,
      iAccount: IAccount
   ): Account {
      val account = Account(
         Account.Id(iAccount.id),
         iAccount.displayName ?: iAccount.username,
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

   private fun convertStatus(
      statusMemo:  MutableMap<String, in Status>,
      accountMemo: MutableMap<String, in Account>,
      iStatus: IStatus
   ): Status {
      if (iStatus.reblog == null) {
         val tooter = convertAccount(accountMemo, iStatus.account)

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
         val booster = convertAccount(accountMemo, iStatus.account)
         val boostedStatus = convertStatus(statusMemo, accountMemo, iStatus.reblog)

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

   private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

   private fun parseDateOrThrow(dateString: String): Date {
      try {
         val utc = apiDateFormat.parse(dateString)
         return Date(utc.time + state.timeZone.rawOffset)
      } catch (e: ParseException) {
         throw IOException("can not parse date: $dateString")
      }
   }
}

// =============================================================================

class ApiGetter : VuexGetter<ApiState>() {
   fun getAccount(id: Account.Id): ReactiveField<Account?>
         = getter { state.accounts()[id] }

   fun getStatus(id: Status.Id): ReactiveField<Status?>
         = getter { state.statuses()[id] }

   fun getCredentialOrThrow(): Credential
         = state.credential() ?: throw IOException()

   internal fun getMastodonInstance(credential: Credential)
         = MastodonInstance(state.kodein, credential.instanceUrl.toExternalForm())
}
