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
import android.view.*
import android.widget.*

class VBinderTestComponent(context: Context) : VComponent() {
   override val componentView: TextView
   val number = vBinder<Int>()

   fun getCurrentNumber() = number()

   init {
      componentView = TextView(context)
      componentView.vBind.text { number().toString() }
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
               activity.setContentView(component.componentView)
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
               activity.setContentView(component.componentView)
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
               activity.setContentView(component.componentView)
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
               activity.setContentView(component.componentView)
            }
            .onActivity {
               assertEquals("0", component.componentView.text)
            }
   }

   @Test fun bindToView_vBinder() {
      class VBinderTestComponent(context: Context) : VComponent() {
         override val componentView: TextView
         val text = vBinder<String>()

         init {
            componentView = TextView(context)
            componentView.vBind.text(text)
         }
      }

      lateinit var component: VBinderTestComponent
      val state = state("0")

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.text(state)
               activity.setContentView(component.componentView)
            }
            .onActivity {
               assertEquals("0", component.componentView.text)
            }
            .onActivity {
               state.value = "1"
            }
            .onActivity {
               assertEquals("1", component.componentView.text)
            }
   }

   @Test fun reactivation() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.componentView)
            }
            .onActivity {
               assertEquals("0", component.componentView.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("1", component.componentView.text)
            }
   }

   @Test fun reactivation_withLambda() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number { state() * 2 }
               activity.setContentView(component.componentView)
            }
            .onActivity {
               assertEquals("0", component.componentView.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("2", component.componentView.text)
            }
   }

   @Test fun getFailure() {
      class GetFailureTestComponent(context: Context) : VComponent() {
         override val componentView = View(context)
         val number = vBinder<Int>()

         fun getCurrentNumber() = number()
      }

      lateinit var component: GetFailureTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = GetFailureTestComponent(activity)
               component.number { throw Exception("Exception from bound reactivatee") }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               val exception = assertFails {
                  component.getCurrentNumber()
               }

               val message = exception.message
               assertNotNull(message)
               assertEquals("Exception from bound reactivatee", message)
            }
   }

   @Test fun boundFailureToView() {
      class BoundFailureToViewTestComponent(context: Context) : VComponent() {
         override val componentView = TextView(context)
         val number = vBinder<Int>()

         init {
            componentView.vBind.text { number().toString() }
         }
      }

      lateinit var component: BoundFailureToViewTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = BoundFailureToViewTestComponent(activity)
               activity.setContentView(component.componentView)
            }
            .onActivity {
               val exception = assertFails {
                  component.number { throw Exception("Exception from bound reactivatee") }
               }

               val message = exception.message
               assertNotNull(message)
               assertEquals("Exception from bound reactivatee", message)
            }
   }

   @Test fun failureAfterBinding() {
      class FailureAfterBindingTestComponent(context: Context) : VComponent() {
         override val componentView = TextView(context)
         val number = vBinder<Int>()

         init {
            componentView.vBind.text { number().toString() }
         }
      }

      lateinit var component: FailureAfterBindingTestComponent
      val state = state(false)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = FailureAfterBindingTestComponent(activity)

               component.number {
                  if (state()) { throw Exception("Exception from bound reactivatee") }
                  0
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               val exception = assertFails {
                  state.value = true
               }

               val message = exception.message
               assertNotNull(message)
               assertEquals("Exception from bound reactivatee", message)
            }
   }
}
