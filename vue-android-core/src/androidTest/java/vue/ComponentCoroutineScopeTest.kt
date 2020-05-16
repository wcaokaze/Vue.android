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
import kotlinx.coroutines.*

class CoroutineScopeTestComponent(context: Context) : VComponent<Nothing>() {
   override val componentView = View(context)
   override val store: Nothing get() = throw UnsupportedOperationException()
}

@RunWith(AndroidJUnit4::class)
class ComponentCoroutineScopeTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun activeWhileMounted() {
      lateinit var component: CoroutineScopeTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = CoroutineScopeTestComponent(activity)
               activity.setContentView(component.componentView)
            }
            .onActivity {
               assertTrue(component.isActive)
            }
   }

   @Test fun inactiveAfterUnmounted() {
      lateinit var containerView: FrameLayout
      lateinit var component: CoroutineScopeTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               component = CoroutineScopeTestComponent(activity)
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { assertTrue(component.isActive) }
            .onActivity { containerView.removeView(component.componentView) }
            .onActivity { assertFalse(component.isActive) }
   }

   @Test fun coroutineIsCancelled_VOnInComponent_whenComponentIsUnmounted() {
      class VOnScopeTestComponent(context: Context) : VComponent<Nothing>() {
         override val componentView = View(context)
         override val store: Nothing get() = throw UnsupportedOperationException()

         init {
            componentView.vOn.click {
               delay(50L)
               throw AssertionError("coroutine has not be cancelled")
            }
         }
      }

      lateinit var containerView: FrameLayout
      lateinit var component: VOnScopeTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               containerView = FrameLayout(activity)
               component = VOnScopeTestComponent(activity)
               containerView.addView(component.componentView)
               activity.setContentView(containerView)
            }
            .onActivity { component.componentView.performClick() }
            .onActivity { containerView.removeView(component.componentView) }
   }
}
