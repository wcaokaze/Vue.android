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

import com.wcaokaze.vue.android.example.mastodon.*
import org.kodein.di.*
import vue.vuex.*

private var store: Store? = null

private fun getStore(kodein: Kodein): Store {
   var s = store
   if (s != null) { return s }

   s = Store(kodein)
   store = s
   return s
}

val KodeinAware.state:    State    get() = getStore(kodein).state
val KodeinAware.mutation: Mutation get() = getStore(kodein).mutation
val KodeinAware.action:   Action   get() = getStore(kodein).action
val KodeinAware.getter:   Getter   get() = getStore(kodein).getter

class Store(private val kodein: Kodein) : VuexStore<State, Mutation, Action, Getter>() {
   object ModuleKeys {
      val MASTODON = Module.Key<MastodonState, MastodonMutation, MastodonAction, MastodonGetter>()
   }

   override fun createState()    = State()
   override fun createMutation() = Mutation()
   override fun createAction()   = Action()
   override fun createGetter()   = Getter()

   override fun createModules() = listOf(
      Module(ModuleKeys.MASTODON, MastodonStore(kodein))
   )
}

class State : VuexState()
class Mutation : VuexMutation<State>()
class Action : VuexAction<State, Mutation, Getter>()
class Getter : VuexGetter<State>()