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
      private constructor(context: Context, private val delegate: StateImpl<T>)
      : VuexState.StateField<T>(), ReactiveField<T> by delegate
{
   constructor(context: Context, default: T) : this(context, StateImpl(default))

   override var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value
      }
}
