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

package vue.vuex

import android.content.*
import androidx.annotation.*
import vue.*
import kotlin.reflect.*

class PreferenceStateDelegate<T>
      internal constructor(
            private val context: Context,
            private val file: PreferenceState.PreferenceFile,
            private val key: String?,
            private val default: T
      )
{
   operator fun getValue(thisRef: Any?, property: KProperty<*>): PreferenceState<T> {
      val key = key ?: property.name
      return PreferenceState(context, file, key, default)
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
      private constructor(context: Context,
                          private val file: PreferenceFile,
                          private val key: String,
                          private val delegate: StateImpl<T>)
      : VuexState.StateField<T>(), ReactiveField<T> by delegate
{
   class PreferenceFile(name: String, mode: Int = Context.MODE_PRIVATE)

   constructor(context: Context, file: PreferenceFile, key: String, default: T)
         : this(context, file, key, StateImpl(default))

   override var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value
      }
}
