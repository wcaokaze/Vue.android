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

@RunWith(AndroidJUnit4::class)
class WatcherTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun watcher() {
      class WatcherTestComponent(context: Context) : VComponent() {
         override val view = View(context)

         val state = state(0)
         var isCalled = false

         init {
            watcher(state) {
               isCalled = true
            }
         }
      }

      lateinit var component: WatcherTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = WatcherTestComponent(activity)
               activity.setContentView(component.view)
            }
            .onActivity { assertFalse(component.isCalled) }
            .onActivity { component.state.value = 1 }
            .onActivity { assertTrue(component.isCalled) }
   }

   @Test fun removeObserver_whenComponentIsUnmounted() {
      val state = state(0)

      class WatcherTestComponent(context: Context) : VComponent() {
         override val view = View(context)

         init {
            watcher(state) {}
         }
      }

      lateinit var containerView: FrameLayout
      lateinit var component: WatcherTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               component = WatcherTestComponent(activity)
               containerView.addView(component.view)
               activity.setContentView(containerView)
            }
            .onActivity { assertEquals(1, state.observerCount) }
            .onActivity { containerView.removeView(component.view) }
            .onActivity { assertEquals(0, state.observerCount) }
   }

   @Test fun reAddObserver_whenComponentIsMounted() {
      val state = state(0)

      class WatcherTestComponent(context: Context) : VComponent() {
         override val view = View(context)

         init {
            watcher(state) {}
         }
      }

      lateinit var containerView: FrameLayout
      lateinit var component: WatcherTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               component = WatcherTestComponent(activity)
               containerView.addView(component.view)
               activity.setContentView(containerView)
            }
            .onActivity { assertEquals(1, state.observerCount) }
            .onActivity { containerView.removeView(component.view) }
            .onActivity { assertEquals(0, state.observerCount) }
            .onActivity { containerView.addView(component.view) }
            .onActivity { assertEquals(1, state.observerCount) }
   }
}
