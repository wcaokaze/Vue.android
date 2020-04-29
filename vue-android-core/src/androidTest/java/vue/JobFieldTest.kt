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
import kotlin.Result

@RunWith(AndroidJUnit4::class)
class JobFieldTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun manuallyGet() {
      val job = GlobalScope.launch {
         delay(50L)
      }

      val reactiveField = job.toReactiveField()

      assertTrue(reactiveField.value)
      Thread.sleep(150L)
      assertFalse(reactiveField.value)
   }

   @Test fun observer() {
      val job = GlobalScope.launch {
         delay(50L)
      }

      val reactiveField = job.toReactiveField()

      var r: Result<Boolean>? = null
      reactiveField.addObserver { r = it }
      Thread.sleep(150L)

      val result = r
      assertNotNull(result)
      assertEquals(false, result.getOrNull())
   }

   @Test fun reactivatee() {
      activityScenarioRule.scenario.onActivity { activity ->
         val job = GlobalScope.launch {
            delay(50L)
         }

         val view = View(activity)
         view.vBind.isVisible { job.toReactiveField()() }

         activity.setContentView(view)

         GlobalScope.launch(Dispatchers.Main) {
            assertEquals(View.VISIBLE, view.visibility)
            delay(150L)
            assertEquals(View.INVISIBLE, view.visibility)
         }
      }
   }
}
