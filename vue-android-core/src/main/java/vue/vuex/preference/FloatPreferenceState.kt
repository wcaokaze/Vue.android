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
 * [PreferenceState] for Float.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun floatPreferenceState(
      context: Context,
      file: PreferenceFile,
      key: String,
      default: Float
) = PreferenceStateDelegate(FloatPreferenceLoader, context, file, key, default)

/**
 * [PreferenceState] for Float.
 * @see [PreferenceState][PreferenceState.Companion.invoke]
 */
fun floatPreferenceState(
      context: Context,
      file: PreferenceFile,
      default: Float
) = PreferenceStateDelegate(FloatPreferenceLoader, context, file, null, default)

object FloatPreferenceLoader : PreferenceState.Loader<Float> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Float): Float
   {
      return sharedPreferences.getFloat(key, default)
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Float)
   {
      sharedPreferences.edit()
            .putFloat(key, value)
            .apply()
   }
}
