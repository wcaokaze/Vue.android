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
 * [PreferenceState] for Long?.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun nullableLongPreferenceState(
      context: Context,
      file: PreferenceFile,
      key: String,
      default: Long?
) = PreferenceStateDelegate(NullableLongPreferenceLoader, context, file, key, default)

/**
 * [PreferenceState] for Long?.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun nullableLongPreferenceState(
      context: Context,
      file: PreferenceFile,
      default: Long?
) = PreferenceStateDelegate(NullableLongPreferenceLoader, context, file, null, default)

object NullableLongPreferenceLoader : PreferenceState.Loader<Long?> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Long?): Long?
   {
      return when (val str = sharedPreferences.getString(key, null)) {
         null   -> default
         "null" -> null
         else   -> str.toLongOrNull() ?: default
      }
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Long?)
   {
      if (value == null) {
         sharedPreferences.edit()
               .putString(key, "null")
               .apply()
      } else {
         sharedPreferences.edit()
               .putString(key, value.toString())
               .apply()
      }
   }
}
