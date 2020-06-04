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

package vue.vuex.preference

import androidx.annotation.*
import vue.*
import vue.vuex.*
import kotlin.reflect.*

/**
 * [PreferenceStateDelegate] for a stub. But this may get ugly.
 *
 * Example with Koin:
 * ```kotlin
 * val realModule = module {
 *    single(named("accessTokenPreference")) {
 *       nullableStringPreferenceState(
 *          context = get(),
 *          file = get(named("preferenceFile")),
 *          key = "accessToken",
 *          default = null)
 *    }
 * }
 *
 * val stubModule = module {
 *    single(named("accessTokenPreference")) {
 *       StubPreferenceState { "STUB" }
 *    }
 * }
 *
 * class CredentialPreferenceState : VuexState(), KoinComponent {
 *    val accessToken by get<PreferenceStateDelegate<String?>>(named("accessTokenPreference"))
 *    //                     ^~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * }
 * ```
 *
 * Note that you can write like the follow (without Delegation, using PreferenceState directly):
 * ```kotlin
 * val realModule = module {
 *    single(named("accessTokenPreference")) {
 *       PreferenceState(NullableStringPreferenceLoader,    // insteadof `nullableStringPreferenceState`
 *          context = get(),
 *          file = get(named("credentialPreferenceFile")),
 *          key = "accessToken",
 *          default = null)
 *    }
 * }
 *
 * val stubModule = module {
 *    single(named("accessTokenPreference")) {
 *       StubPreferenceState { "STUB" }
 *    }
 * }
 *
 * class CredentialPreferenceState : VuexState(), KoinComponent {
 *    val accessToken: PreferenceState<String?> = get(named("accessTokenPreference"))
 *    //                                        ^ Not `by`
 * }
 * ```
 */
class StubPreferenceState<T>
      private constructor(private val saver: (T) -> Unit,
                          private val delegate: StateImpl<T>)
      : PreferenceState<T>(),
      PreferenceStateDelegate<T>,
      ReactiveField<T> by delegate
{
   constructor(loader: () -> T) : this({}, StateImpl(loader()))

   constructor(loader: () -> T, saver: (T) -> Unit)
         : this(saver, StateImpl(loader()))

   override operator fun getValue(thisRef: Any?, property: KProperty<*>) = this

   override var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value
         saver(value)
      }
}
