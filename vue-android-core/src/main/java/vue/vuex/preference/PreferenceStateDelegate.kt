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
import kotlin.reflect.*

class PreferenceStateDelegate<T>(
      private val loader: PreferenceState.Loader<T>,
      private val context: Context,
      private val file: PreferenceFile,
      private val key: String?,
      private val default: T
) {
   private var preferenceState: PreferenceState<T>? = null

   operator fun getValue(thisRef: Any?, property: KProperty<*>): PreferenceState<T> {
      if (preferenceState != null) { return preferenceState!! }

      synchronized (this) {
         if (preferenceState != null) { return preferenceState!! }

         val key = key ?: property.name
         val ps = PreferenceState(loader, context, file, key, default)
         preferenceState = ps
         return ps
      }
   }
}
