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

import android.content.*
import androidx.annotation.*
import vue.*
import vue.vuex.*

/**
 * [StateField][VuexState.StateField] to automatically save/load from [SharedPreferences]
 * ```kotlin
 * class AppearancePreferenceState(private val context: Context) : VuexState() {
 *    private val file = PreferenceFile("credential")
 *
 *    val mainTextSize by intPreferenceState(context, file, default = 14)
 *    val iconSize by intPreferenceState(context, file, default = 48)
 *
 *    val primaryTextColor by intPreferenceState(context, file, default = 0xff2196f3.toInt())
 *    //                   ^^
 *    // Note that we must use `by` here. not `=`
 * }
 * ```
 *
 * Of course this is [ReactiveField]. We can bind them to Views.
 * ```kotlin
 * textView.vBind.textColor(state.primaryTextColor)
 * ```
 *
 * @param context Context. This State should not live longer than Context.
 *   The easiest way to ensure it is using Application as Context,
 *   and let Application have the VuexStore.
 *   ```kotlin
 *   class MyApplication : Application() {
 *      val myStore = MyStore(this)
 *   }
 *   ```
 */
class PreferenceState<T>
      private constructor(private val loader: Loader<T>,
                          private val context: Context,
                          private val file: PreferenceFile,
                          private val key: String,
                          private val delegate: StateImpl<T>)
      : VuexState.StateField<T>(), ReactiveField<T> by delegate
{
   interface Loader<T> {
      fun get(sharedPreferences: SharedPreferences, key: String, default: T): T
      fun put(sharedPreferences: SharedPreferences, key: String, value: T)
   }

   constructor(
         loader: Loader<T>,
         context: Context,
         file: PreferenceFile,
         key: String,
         default: T
   ) : this(
         loader,
         context,
         file,
         key,
         StateImpl(
               loader.get(
                     context.getSharedPreferences(file.name, file.mode),
                     key,
                     default
               )
         )
   )

   override var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value

         loader.put(
               context.getSharedPreferences(file.name, file.mode),
               key,
               value
         )
      }
}
