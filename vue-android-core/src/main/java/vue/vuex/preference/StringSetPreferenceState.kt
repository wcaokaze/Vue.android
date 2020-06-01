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

/**
 * [PreferenceState] for Set<String>.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun stringSetPreferenceState(
      context: Context,
      file: PreferenceFile,
      key: String,
      default: Set<String>
) = PreferenceStateDelegate(StringSetPreferenceLoader, context, file, key, default)

/**
 * [PreferenceState] for Set<String>.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun stringSetPreferenceState(
      context: Context,
      file: PreferenceFile,
      default: Set<String>
) = PreferenceStateDelegate(StringSetPreferenceLoader, context, file, null, default)

object StringSetPreferenceLoader : PreferenceState.Loader<Set<String>> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Set<String>): Set<String>
   {
      return sharedPreferences.getStringSet(key, default) ?: default
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Set<String>)
   {
      sharedPreferences.edit()
            .putStringSet(key, value)
            .apply()
   }
}
