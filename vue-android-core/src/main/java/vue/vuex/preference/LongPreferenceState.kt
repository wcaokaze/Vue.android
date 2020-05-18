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
 * [PreferenceState] for Long.
 * @see PreferenceState
 */
fun longPreferenceState(
      context: Context,
      file: PreferenceFile,
      key: String,
      default: Long
) = PreferenceStateDelegate(LongPreferenceLoader, context, file, key, default)

/**
 * [PreferenceState] for Long.
 * @see PreferenceState
 */
fun longPreferenceState(
      context: Context,
      file: PreferenceFile,
      default: Long
) = PreferenceStateDelegate(LongPreferenceLoader, context, file, null, default)

object LongPreferenceLoader : PreferenceState.Loader<Long> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Long): Long
   {
      return sharedPreferences.getLong(key, default)
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Long)
   {
      sharedPreferences.edit()
            .putLong(key, value)
            .apply()
   }
}
