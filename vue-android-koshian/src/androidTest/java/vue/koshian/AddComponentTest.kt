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

package vue.koshian

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.view.*
import koshian.*
import vue.*
import kotlin.contracts.*

@RunWith(AndroidJUnit4::class)
class AddComponentTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   class AddComponentTestComponent(context: Context) : VComponent() {
      override val componentView = View(context)
   }

   @Test fun addComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val componentAsResult: AddComponentTestComponent
         val componentInDsl: AddComponentTestComponent

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               componentAsResult = Component(AddComponentTestComponent(activity)) {
                  componentInDsl = component
               }
            }
         }

         assertSame(componentInDsl, componentAsResult)
         assertSame(rootView.getChildAt(0), componentAsResult.componentView)
      }
   }

   @Test fun applyComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val component1: AddComponentTestComponent

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               component1 = Component(AddComponentTestComponent(activity)) {
               }
            }
         }

         rootView.applyKoshian {
            Component(component1) {
               assertSame(component1, component)
            }
         }
      }
   }
}
