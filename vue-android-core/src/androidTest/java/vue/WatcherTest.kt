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
      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

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
               activity.setContentView(component.componentView)
            }
            .onActivity { assertFalse(component.isCalled) }
            .onActivity { component.state.value = 1 }
            .onActivity { assertTrue(component.isCalled) }
   }

   @Test fun removeObserver_whenComponentIsUnmounted() {
      val state = state(0)

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

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
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { assertEquals(1, state.observerCount) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(0, state.observerCount) }
   }

   @Test fun reAddObserver_whenComponentIsMounted() {
      val state = state(0)

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

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
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { assertEquals(1, state.observerCount) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(0, state.observerCount) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, state.observerCount) }
   }

   @Test fun noImmediate() {
      val state = state(0)
      var wasCalled = false

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            watcher(state, immediate = false) {
               wasCalled = true
            }
         }
      }

      activityScenarioRule.scenario
            .onActivity { activity ->
               val containerView = FrameLayout(activity)
               val component = WatcherTestComponent(activity)
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { assertFalse(wasCalled) }
   }

   @Test fun immediate() {
      val state = state(0)
      var wasCalled = false

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            watcher(state, immediate = true) {
               wasCalled = true
            }
         }
      }

      activityScenarioRule.scenario
            .onActivity { activity ->
               val containerView = FrameLayout(activity)
               val component = WatcherTestComponent(activity)
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { assertTrue(wasCalled) }
   }

   @Test fun immediate_butNotAttached() {
      val state = state(0)
      var wasCalled = false

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            watcher(state, immediate = true) {
               wasCalled = true
            }
         }
      }

      activityScenarioRule.scenario
            .onActivity { activity ->
               WatcherTestComponent(activity)
            }
            .onActivity { assertFalse(wasCalled) }
   }

   @Test fun immediate_attachedLater() {
      val state = state(0)
      var wasCalled = false

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            watcher(state, immediate = true) {
               wasCalled = true
            }
         }
      }

      lateinit var component: WatcherTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = WatcherTestComponent(activity)
            }
            .onActivity { assertFalse(wasCalled) }
            .onActivity { activity ->
               activity.setContentView(component.componentView)
            }
            .onActivity { assertTrue(wasCalled) }
   }

   @Test fun immediate_attachedTwice() {
      val state = state(0)
      var calledCount = 0

      class WatcherTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            watcher(state, immediate = true) {
               calledCount++
            }
         }
      }

      lateinit var containerView: FrameLayout
      lateinit var component: WatcherTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               component = WatcherTestComponent(activity)
               activity.setContentView(containerView)
            }
            .onActivity { assertEquals(0, calledCount) }
            .onActivity {
               containerView.addView(component.componentView)
            }
            .onActivity { assertEquals(1, calledCount) }
            .onActivity {
               containerView.removeView(component.componentView)
            }
            .onActivity { assertEquals(1, calledCount) }
            .onActivity {
               containerView.addView(component.componentView)
            }
            .onActivity { assertEquals(1, calledCount) }
   }
}
