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
import kotlin.reflect.*

class PreferenceStateDelegate<T>(
      private val loader: PreferenceState.Loader<T>,
      private val context: Context,
      private val file: PreferenceState.PreferenceFile,
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

/**
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
                          context: Context,
                          private val file: PreferenceFile,
                          private val key: String,
                          private val delegate: StateImpl<T>)
      : VuexState.StateField<T>(), ReactiveField<T> by delegate
{
   interface Loader<T> {
      fun get(sharedPreferences: SharedPreferences, key: String, default: T): T
      fun put(sharedPreferences: SharedPreferences, key: String, value: T)
   }

   class PreferenceFile(val name: String, val mode: Int = Context.MODE_PRIVATE)

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
      }
}

fun intPreferenceState(context: Context, file: PreferenceState.PreferenceFile, key: String, default: Int) = PreferenceStateDelegate(IntPreferenceLoader, context, file, key,  default)
fun intPreferenceState(context: Context, file: PreferenceState.PreferenceFile,              default: Int) = PreferenceStateDelegate(IntPreferenceLoader, context, file, null, default)

object IntPreferenceLoader : PreferenceState.Loader<Int> {
   override fun get(sharedPreferences: SharedPreferences,
                    key: String,
                    default: Int): Int
   {
      return sharedPreferences.getInt(key, default)
   }

   override fun put(sharedPreferences: SharedPreferences,
                    key: String,
                    value: Int)
   {
      sharedPreferences.edit()
            .putInt(key, value)
            .apply()
   }
}
