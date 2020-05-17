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
import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import vue.*
import kotlin.test.*
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class PreferenceStateTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun loadPreference() {
      activityScenarioRule.scenario.onActivity { activity ->
         val fileName = "loadPreference"
         val file = PreferenceState.PreferenceFile(fileName)

         activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)
               .edit()
               .putInt("state", 42)
               .commit()

         val state by intPreferenceState(activity, file, default = 0)

         assertEquals(42, state.value)
      }
   }

   @Test fun useDefault() {
      activityScenarioRule.scenario.onActivity { activity ->
         val fileName = "useDefault"

         removeSharedPreference(activity, fileName, "state")

         val file = PreferenceState.PreferenceFile(fileName)
         val state by intPreferenceState(activity, file, default = 42)

         assertEquals(42, state.value)
      }
   }

   @Test fun savePreference() {
      activityScenarioRule.scenario.onActivity { activity ->
         val fileName = "savePreference"

         removeSharedPreference(activity, fileName, "state")

         val file = PreferenceState.PreferenceFile(fileName)
         val state by intPreferenceState(activity, file, default = 0)

         state.value = 42

         val savedValue = activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)
               .getInt("state", 0)

         assertEquals(42, savedValue)
      }
   }

   @Test fun reactivation() {
      activityScenarioRule.scenario.onActivity { activity ->
         val fileName = "reactivation"

         removeSharedPreference(activity, fileName, "state")

         val file = PreferenceState.PreferenceFile(fileName)
         val state by intPreferenceState(activity, file, default = 0)

         var i = -1
         state.addObserver { i = it.getOrThrow() }
         state.value = 1

         assertEquals(1, i)
      }
   }

   private fun removeSharedPreference(context: Context, fileName: String, key: String) {
      context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            .edit()
            .remove(key)
            .commit()
   }
}

