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

import android.text.*
import android.widget.*
import androidx.lifecycle.*

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

   @Test fun bind_subtypeOfInput() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<CharSequence>("")
         val editText = EditText(activity)
         editText.vModel.text(state)
      }
   }

   @Test fun bind_supertypeOfOutput() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<Spannable?>(null)
         val editText = EditText(activity)
         editText.vModel.text(state)
      }
   }

   // should not compile
   /*
   @Test fun bind_supertypeOfInput() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<Any?>(null)
         val editText = EditText(activity)
         editText.vModel.text(state)
      }
   }
   */

   // should not compile
   /*
   @Test fun bind_subtypeOfOutput() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(SpannableStringBuilder())
         val editText = EditText(activity)
         editText.vModel.text(state)
      }
   }
   */

   @Test fun bind_initialValue() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<CharSequence?>("Vue.android")
         val editText = EditText(activity)

         editText.vModel.text(state)
         activity.setContentView(editText)

         assertEquals("Vue.android", editText.text.toString())
      }
   }

   @Test fun shouldNotBind_ifViewNotDisplayed() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<CharSequence?>(null)
         val editText = EditText(activity)
         editText.vModel.text(state)
         // activity.setContentView(view)

         assertEquals(0, state.observerCount)
      }
   }

   @Test fun unbind_onViewRemoved() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state<CharSequence?>(null)
         val parentView = LinearLayout(activity)
         val editText = EditText(activity)
         parentView.addView(editText)
         editText.vModel.text(state)
         activity.setContentView(parentView)

         assertEquals(1, state.observerCount)
         parentView.removeView(editText)
         assertEquals(0, state.observerCount)
      }
   }

   @Test fun unbind_onActivityFinish() {
      val state = state<CharSequence?>(null)

      activityScenarioRule.scenario.onActivity { activity ->
         val editText = EditText(activity)
         editText.vModel.text(state)
         activity.setContentView(editText)
      }

      assertEquals(1, state.observerCount)
      activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)
      assertEquals(0, state.observerCount)
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
