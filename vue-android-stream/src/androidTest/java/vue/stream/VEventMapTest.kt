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

package vue.stream

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.widget.*
import kotlinx.coroutines.*
import vue.*

@RunWith(AndroidJUnit4::class)
class VEventMapTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun map() {
      lateinit var editText: EditText
      var i = -1

      activityScenarioRule.scenario
            .onActivity { activity ->
               editText = EditText(activity)
               editText.vOn.textChanged.map { it.length } .invoke { i = it }
            }
            .onActivity { editText.setText("Vue.android") }
            .onActivity { assertEquals(11, i) }
   }

   @Test fun mapperCanSuspend() {
      lateinit var editText: EditText
      var i = -1

      activityScenarioRule.scenario
            .onActivity { activity ->
               editText = EditText(activity)

               editText.vOn.textChanged
                     .map {
                        delay(50L)
                        it.length
                     }
                     .invoke { i = it }
            }
            .onActivity { editText.setText("Vue.android") }

      Thread.sleep(100L)

      activityScenarioRule.scenario.onActivity { assertEquals(11, i) }
   }
}
