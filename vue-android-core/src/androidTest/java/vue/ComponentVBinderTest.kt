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

import android.content.*
import android.widget.*

class VBinderTestComponent(context: Context) : VComponent() {
   override val view: TextView
   val number = vBinder<Int>()

   fun getCurrentNumber() = number()

   init {
      view = TextView(context)
      view.vBind.text { number().toString() }
   }
}

@RunWith(AndroidJUnit4::class)
class ComponentVBinderTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun initialValueNull() {
      lateinit var component: VBinderTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(null, component.getCurrentNumber())
            }
   }

   @Test fun bindValue_state() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(0, component.getCurrentNumber())
            }
   }

   @Test fun bindValue_reactivatee() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number { state() + 1 }
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(1, component.getCurrentNumber())
            }
   }

   @Test fun bindToView() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
   }

   @Test fun bindToView_vBinder() {
      class VBinderTestComponent(context: Context) : VComponent() {
         override val view: TextView
         val text = vBinder<String>()

         init {
            view = TextView(context)
            view.vBind.text(text)
         }
      }

      lateinit var component: VBinderTestComponent
      val state = state("0")

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.text(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = "1"
            }
            .onActivity {
               assertEquals("1", component.view.text)
            }
   }

   @Test fun reactivation() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("1", component.view.text)
            }
   }

   @Test fun reactivation_withLambda() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number { state() * 2 }
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("2", component.view.text)
            }
   }
}
