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

class LifecycleTestComponent(context: Context) : VComponent<Nothing>() {
   override val componentView = View(context)
   override val store: Nothing get() = throw UnsupportedOperationException()
}

@RunWith(AndroidJUnit4::class)
class ComponentLifecycleTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun onAttachedToActivity() {
      lateinit var component: LifecycleTestComponent
      var attached = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = LifecycleTestComponent(activity)

               component.componentLifecycle.onAttachedToActivity += {
                  attached = true
               }
            }
            .onActivity { assertFalse(attached) }
            .onActivity { activity -> activity.setContentView(component.componentView) }
            .onActivity { assertTrue(attached) }
   }

   @Test fun onAttachedToActivity_twice() {
      lateinit var containerView: FrameLayout
      lateinit var component: LifecycleTestComponent
      var count = 0

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               activity.setContentView(containerView)

               component = LifecycleTestComponent(activity)

               component.componentLifecycle.onAttachedToActivity += {
                  count++
               }
            }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(2, count) }
   }

   @Test fun onAttachedToActivity_removeListener() {
      lateinit var containerView: FrameLayout
      lateinit var component: LifecycleTestComponent
      var count = 0
      val listener = fun () { count++ }

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               activity.setContentView(containerView)

               component = LifecycleTestComponent(activity)
               component.componentLifecycle.onAttachedToActivity += listener
            }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { component.componentLifecycle.onAttachedToActivity -= listener }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, count) }
   }

   @Test fun onDetachedFromActivity() {
      lateinit var containerView: FrameLayout
      lateinit var component: LifecycleTestComponent
      var detached = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               activity.setContentView(containerView)

               component = LifecycleTestComponent(activity)

               component.componentLifecycle.onDetachedFromActivity += {
                  detached = true
               }
            }
            .onActivity { assertFalse(detached) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertFalse(detached) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertTrue(detached) }
   }

   @Test fun onDetachedActivity_twice() {
      lateinit var containerView: FrameLayout
      lateinit var component: LifecycleTestComponent
      var count = 0

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               activity.setContentView(containerView)

               component = LifecycleTestComponent(activity)

               component.componentLifecycle.onDetachedFromActivity += {
                  count++
               }
            }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(2, count) }
   }

   @Test fun onDetachedActivity_removeListener() {
      lateinit var containerView: FrameLayout
      lateinit var component: LifecycleTestComponent
      var count = 0
      val listener = fun () { count++ }

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               activity.setContentView(containerView)

               component = LifecycleTestComponent(activity)
               component.componentLifecycle.onDetachedFromActivity += listener
            }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(0, count) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.addView(component.componentView) }
            .onActivity { assertEquals(1, count) }
            .onActivity { component.componentLifecycle.onDetachedFromActivity -= listener }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertEquals(1, count) }
   }
}
