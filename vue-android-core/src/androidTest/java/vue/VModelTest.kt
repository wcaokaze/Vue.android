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

package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.widget.*

@RunWith(AndroidJUnit4::class)
class VModelTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun bind() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<CharSequence?>(null)
         val editText = EditText(activity)
         editText.vModel.text(state)
         activity.setContentView(editText)

         assertEquals(1, state.observerCount)
      }
   }

   @Test fun stateToView() {
      val state = state<CharSequence?>(null)
      lateinit var editText: EditText

      activityScenarioRule.scenario
            .onActivity { activity ->
               editText = EditText(activity)
               editText.vModel.text(state)
               activity.setContentView(editText)
            }
            .onActivity {
               state.value = "Vue.android"
            }
            .onActivity {
               assertEquals("Vue.android", editText.text.toString())
            }
   }

   @Test fun viewToState() {
      val state = state<CharSequence?>(null)
      lateinit var editText: EditText

      activityScenarioRule.scenario
            .onActivity { activity ->
               editText = EditText(activity)
               editText.vModel.text(state)
               activity.setContentView(editText)
            }
            .onActivity {
               editText.setText("Vue.android")
            }
            .onActivity {
               assertEquals("Vue.android", state.value.toString())
            }
   }
}
