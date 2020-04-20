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
import kotlinx.coroutines.*
import kotlin.Result

@RunWith(AndroidJUnit4::class)
class DeferredFieldTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun manuallyGet_success() {
      val deferred = GlobalScope.async {
         delay(50L)
         "Deferred Result"
      }

      val reactiveField = deferred.toReactiveField()

      assertNull(reactiveField.value)
      Thread.sleep(150L)
      assertEquals("Deferred Result", reactiveField.value)
   }

   @Test fun manuallyGet_failure() {
      val deferred = GlobalScope.async {
         delay(50L)
         throw Exception("Exception from Deferred")
      }

      val reactiveField = deferred.toReactiveField()

      assertNull(reactiveField.value)
      Thread.sleep(150L)

      val exception = assertFails {
         reactiveField.value
      }

      val message = exception.message
      assertNotNull(message)
      assertEquals("Exception from Deferred", message)
   }

   @Test fun observer_success() {
      val deferred = GlobalScope.async {
         delay(50L)
         "Deferred Result"
      }

      val reactiveField = deferred.toReactiveField()

      var r: Result<String?>? = null
      reactiveField.addObserver { r = it }
      Thread.sleep(150L)

      val result = r
      assertNotNull(result)
      assertEquals("Deferred Result", result.getOrNull())
   }

   @Test fun observer_failure() {
      val deferred = GlobalScope.async {
         delay(50L)
         throw Exception("Exception from Deferred")
      }

      val reactiveField = deferred.toReactiveField()

      var r: Result<String?>? = null
      reactiveField.addObserver { r = it }
      Thread.sleep(150L)

      val result = r
      assertNotNull(result)
      assertTrue(result.isFailure)
      val exception = result.exceptionOrNull()
      val message = exception?.message
      assertNotNull(message)
      assertEquals("Exception from Deferred", message)
   }

   @Test fun reactivatee_success() {
      activityScenarioRule.scenario.onActivity { activity ->
         val deferred = GlobalScope.async {
            delay(50L)
            "Deferred Result"
         }

         val textView = TextView(activity)
         textView.vBind.text { deferred.toReactiveField()() }

         activity.setContentView(textView)

         GlobalScope.launch(Dispatchers.Main) {
            delay(150L)
            assertEquals("Deferred Result", textView.text)
         }
      }
   }

   @Test fun reactivatee_failure() {
      activityScenarioRule.scenario.onActivity { activity ->
         val deferred = GlobalScope.async {
            delay(50L)
            throw Exception("Exception from Deferred")
         }

         val textView = TextView(activity)

         lateinit var exception: Exception

         textView.vBind.text {
            try {
               deferred.toReactiveField()()
            } catch (e: Exception) {
               exception = e
               ""
            }
         }

         activity.setContentView(textView)

         GlobalScope.launch(Dispatchers.Main) {
            delay(150L)

            val message = exception.message
            assertEquals("Exception from Deferred", message)
         }
      }
   }
}
