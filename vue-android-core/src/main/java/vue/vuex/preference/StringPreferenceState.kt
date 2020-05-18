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
 * [PreferenceState] for String.
 * @see PreferenceState
 */
fun stringPreferenceState(
      context: Context,
      file: PreferenceFile,
      key: String,
      default: String
) = PreferenceStateDelegate(StringPreferenceLoader, context, file, key, default)

/**
 * [PreferenceState] for String.
 * @see PreferenceState
 */
fun stringPreferenceState(
      context: Context,
      file: PreferenceFile,
      default: String
) = PreferenceStateDelegate(StringPreferenceLoader, context, file, null, default)

object StringPreferenceLoader : PreferenceState.Loader<String> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: String): String
   {
      return sharedPreferences.getString(key, default) ?: default
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: String)
   {
      sharedPreferences.edit()
            .putString(key, value)
            .apply()
   }
}
