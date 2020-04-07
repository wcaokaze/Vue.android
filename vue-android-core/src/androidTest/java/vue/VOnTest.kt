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

import android.view.*
import kotlinx.coroutines.*

@RunWith(AndroidJUnit4::class)
class VOnTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun bind() {
      var isCalled = false
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)
               view.vOn.click { isCalled = true }
               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }

      assertTrue(isCalled)
   }

   // No assertions, always passes. This test is only to test compilability.
   @Test fun actionCanCallSuspendFun() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         view.vOn.click { delay(1L) }
         activity.setContentView(view)
      }
   }

   @Test fun actionIsCalledOnMainThread() {
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }

   @Test fun coroutineContext() {
      val job = Job()
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(job) {
                  delay(100L)
                  fail("The action is not cancelled")
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
            .onActivity {
               job.cancel()
            }

      Thread.sleep(200L)
   }

   @Test fun actionIsCalledOnMainThread_evenIfCoroutineContextSpecified() {
      val job = Job()
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(job) {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }

   @Test fun dispatcherCanBeOverwritten() {
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(Dispatchers.Default) {
                  assertFalse(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }
}
