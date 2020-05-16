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
import kotlinx.coroutines.*

@RunWith(AndroidJUnit4::class)
class ComponentVEventTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun emitManually() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click { onClick.emit() }
         }
      }

      lateinit var component: Component
      var isCalled = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)
               component.onClick { isCalled = true }
               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }

      assertTrue(isCalled)
   }

   @Test fun bindToAnotherVEvent() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(onClick)
         }
      }

      lateinit var component: Component
      var isCalled = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)
               component.onClick { isCalled = true }
               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }

      assertTrue(isCalled)
   }

   @Test fun actionIsCalledOnMainThread() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(onClick)
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }
   }

   @Test fun actionIsCalledOnMainThread_evenIfAnotherDispatcherIsSpecifiedInComponent() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(Dispatchers.Default) { onClick.emit() }
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }
   }

   @Test fun coroutineContext() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(onClick)
         }
      }

      val job = Job()
      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(job) {
                  delay(100L)
                  fail("The action is not cancelled")
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }
            .onActivity {
               job.cancel()
            }

      Thread.sleep(200L)
   }

   @Test fun actionIsCalledOnMainThread_evenIfCoroutineContextSpecified() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(onClick)
         }
      }

      val job = Job()
      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(job) {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }
   }

   @Test fun dispatcherCanBeOverwritten() {
      class Component(context: Context) : VComponent<Nothing>() {
         override val componentView: View
         override val store: Nothing get() = throw UnsupportedOperationException()

         val onClick = vEvent0()

         init {
            componentView = View(context)
            componentView.vOn.click(onClick)
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(Dispatchers.Default) {
                  assertFalse(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.componentView)
            }
            .onActivity {
               component.componentView.performClick()
            }
   }
}
