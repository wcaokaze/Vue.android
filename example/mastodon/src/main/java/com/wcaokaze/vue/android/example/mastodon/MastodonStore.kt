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

import android.graphics.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.statuses.*
import com.wcaokaze.vue.android.example.mastodon.infrastructure.v1.timelines.*
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.koin.core.*
import org.koin.core.parameter.*
import org.koin.core.qualifier.*
import vue.*
import vue.vuex.*
import java.io.*
import java.util.*
import kotlin.collections.*

class MastodonStore
   : VuexStore<MastodonState, MastodonMutation, MastodonAction, MastodonGetter>()
{
   override fun createState()    = MastodonState()
   override fun createMutation() = MastodonMutation()
   override fun createAction()   = MastodonAction()
   override fun createGetter()   = MastodonGetter()
}

// =============================================================================

class MastodonState : VuexState(), KoinComponent {
   val timeZone: TimeZone by inject()

   val credential = state<Credential?>(null)

   val accounts = state<Map<Account.Id, Account>>(emptyMap())
   val accountIcons = state<Map<Account.Id, Bitmap?>>(emptyMap())

   val statuses = state<Map<Status .Id, Status>>(emptyMap())
}

// =============================================================================

class MastodonMutation : VuexMutation<MastodonState>() {
   fun setCredential(credential: Credential) {
      state.credential.value = credential
      state.accounts.value = emptyMap()
      state.statuses.value = emptyMap()
   }

   fun addAccounts(accounts: Iterable<Account>) {
      state.accounts.value =
         state.accounts.value + accounts.asSequence().map { it.id to it }
   }

   fun addAccountIcon(accountId: Account.Id, accountIcon: Bitmap?) {
      state.accountIcons.value =
         state.accountIcons.value + (accountId to accountIcon)
   }

   fun addStatuses(statuses: Iterable<Status>) {
      state.statuses.value =
         state.statuses.value + statuses.asSequence().map { it.id to it }
   }

   internal fun addAllConvertedEntities(entityConverter: EntityConverter) {
      addAccounts(entityConverter.getAllConvertedAccounts())
      addStatuses(entityConverter.getAllConvertedStatuses())
   }
}

// =============================================================================

class MastodonAction
   : VuexAction<MastodonState, MastodonMutation, MastodonGetter>(),
   KoinComponent
{
   private val httpClient: HttpClient by inject()

   private fun getStatusService(credential: Credential): StatusService
         = get { parametersOf(credential) }

   private fun getTimelineService(credential: Credential): TimelineService
         = get { parametersOf(credential) }

   suspend fun fetchHomeTimeline(
      maxId: Status.Id? = null,
      sinceId: Status.Id? = null,
      statusCountLimit: Int
   ): List<Status.Id> {
      val credential = getter.getCredentialOrThrow()
      val timelineService = getTimelineService(credential)

      val iStatuses = timelineService.fetchHomeTimeline(
         maxId = maxId?.id,
         sinceId = sinceId?.id,
         limit = statusCountLimit
      )

      val converter = EntityConverter(state.timeZone)
      val statusIds = iStatuses.map { converter.convertStatus(it).id }
      mutation.addAllConvertedEntities(converter)

      GlobalScope.launch(Dispatchers.Main) {
         for (account in converter.getAllConvertedAccounts()) {
            fetchAccountIcon(account)
         }
      }

      return statusIds
   }

   suspend fun fetchAccountIcon(account: Account) {
      if (account.iconUrl == null) {
         mutation.addAccountIcon(account.id, null)
         throw CancellationException()
      }

      val bitmapByteArray: ByteArray = try {
         httpClient.get(account.iconUrl)
      } catch (e: CancellationException) {
         throw e
      } catch (e: Exception) {
         mutation.addAccountIcon(account.id, null)
         throw CancellationException()
      }

      val bitmap = BitmapFactory
         .decodeByteArray(bitmapByteArray, 0, bitmapByteArray.size)

      mutation.addAccountIcon(account.id, bitmap)
   }

   suspend fun boost(statusId: Status.Id) {
      val credential = getter.getCredentialOrThrow()
      val statusService = getStatusService(credential)

      val iStatus = statusService.reblogStatus(statusId.id)

      val converter = EntityConverter(state.timeZone)
      converter.convertStatus(iStatus)
      mutation.addAllConvertedEntities(converter)
   }

   suspend fun unboost(statusId: Status.Id) {
      val credential = getter.getCredentialOrThrow()
      val statusService = getStatusService(credential)

      val iStatus = statusService.unreblogStatus(statusId.id)

      val converter = EntityConverter(state.timeZone)
      converter.convertStatus(iStatus)
      mutation.addAllConvertedEntities(converter)
   }

   suspend fun favorite(statusId: Status.Id) {
      val credential = getter.getCredentialOrThrow()
      val statusService = getStatusService(credential)

      val iStatus = statusService.favouriteStatus(statusId.id)

      val converter = EntityConverter(state.timeZone)
      converter.convertStatus(iStatus)
      mutation.addAllConvertedEntities(converter)
   }

   suspend fun unfavorite(statusId: Status.Id) {
      val credential = getter.getCredentialOrThrow()
      val statusService = getStatusService(credential)

      val iStatus = statusService.unfavouriteStatus(statusId.id)

      val converter = EntityConverter(state.timeZone)
      converter.convertStatus(iStatus)
      mutation.addAllConvertedEntities(converter)
   }
}

// =============================================================================

class MastodonGetter : VuexGetter<MastodonState>() {
   fun getAccount(id: Account.Id): ReactiveField<Account?>
         = getter { state.accounts()[id] }

   fun getAccountIcon(id: Account.Id): ReactiveField<Bitmap?>
         = getter { state.accountIcons()[id] }

   fun getStatus(id: Status.Id): ReactiveField<Status?>
         = getter { state.statuses()[id] }

   fun getCredentialOrThrow(): Credential
         = state.credential() ?: throw IOException()
}
