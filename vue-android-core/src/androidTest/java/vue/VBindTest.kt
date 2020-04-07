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
import android.widget.*
import androidx.lifecycle.*

@RunWith(AndroidJUnit4::class)
class VBindTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun bind() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val view = View(activity)
         view.vBind.isVisible(state)
         activity.setContentView(view)

         assertEquals(1, state.observerCount)
      }
   }

   @Test fun bind_initialValue() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val view = View(activity)

         view.visibility = View.VISIBLE
         view.vBind.isVisible(state)
         activity.setContentView(view)

         assertEquals(View.INVISIBLE, view.visibility)
      }
   }

   @Test fun shouldNotBind_ifViewNotDisplayed() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val view = View(activity)
         view.vBind.isVisible(state)
         // activity.setContentView(view)

         assertEquals(0, state.observerCount)
      }
   }

   @Test fun unbind_onViewRemoved() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val parentView = LinearLayout(activity)
         val view = View(activity)
         parentView.addView(view)
         view.vBind.isVisible(state)
         activity.setContentView(parentView)

         assertEquals(1, state.observerCount)
         parentView.removeView(view)
         assertEquals(0, state.observerCount)
      }
   }

   @Test fun unbind_onActivityFinish() {
      val state = state(false)

      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         view.vBind.isVisible(state)
         activity.setContentView(view)
      }

      assertEquals(1, state.observerCount)
      activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)
      assertEquals(0, state.observerCount)
   }

   @Test fun vBind_returnsSameInstanceTwice() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         val firstVBind  = view.vBind
         val secondVBind = view.vBind
         assertSame(firstVBind, secondVBind)
      }
   }

   @Test fun vBind_differenceInstanceForDifferenceView() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view1 = View(activity)
         val view2 = View(activity)
         val vBind1 = view1.vBind
         val vBind2 = view2.vBind
         assertNotSame(vBind1, vBind2)
      }
   }

   @Test fun isVisible_returnsSameInstanceTwice() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         val firstIsVisible  = view.vBind.isVisible
         val secondIsVisible = view.vBind.isVisible
         assertSame(firstIsVisible, secondIsVisible)
      }
   }

   @Test fun bind_unbindOldReactiveField() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state1 = state(false)
         val state2 = state(false)

         val view = View(activity)
         view.vBind.isVisible(state1)
         activity.setContentView(view)

         assertEquals(1, state1.observerCount)
         assertEquals(0, state2.observerCount)
         view.vBind.isVisible(state2)
         assertEquals(0, state1.observerCount)
         assertEquals(1, state2.observerCount)
      }
   }

   @Test fun bind_withLambda() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val view = View(activity)
         view.vBind.isVisible { state() }
         activity.setContentView(view)

         assertEquals(1, state.observerCount)
      }
   }

   @Test fun unbind_withLambda() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)
         val parentView = LinearLayout(activity)
         val view = View(activity)
         parentView.addView(view)
         view.vBind.isVisible { state() }
         activity.setContentView(parentView)

         assertEquals(1, state.observerCount)
         parentView.removeView(view)
         assertEquals(0, state.observerCount)
      }
   }

   @Test fun nonReactiveValue() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         view.visibility = View.VISIBLE
         view.vBind.isVisible(false)
         activity.setContentView(view)

         assertEquals(View.INVISIBLE, view.visibility)
      }
   }

   @Test fun nonReactiveValue_unbindOldReactiveField() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = state(false)

         val view = View(activity)
         view.vBind.isVisible(state)
         activity.setContentView(view)

         assertEquals(1, state.observerCount)
         view.vBind.isVisible(false)
         assertEquals(0, state.observerCount)
      }
   }
}
