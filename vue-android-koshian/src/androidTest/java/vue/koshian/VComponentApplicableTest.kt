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

val CreatorParent<*>.TestComponent: VComponentApplicable<VComponentApplicableTest.TestComponent> get() {
   val component = VComponentApplicableTest.TestComponent(context)
   return VComponentApplicable(component)
}

@RunWith(AndroidJUnit4::class)
class VComponentApplicableTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   class TestComponent(context: Context) : VComponent<Nothing>() {
      override val componentView = View(context)
      override val store: Nothing get() = throw UnsupportedOperationException()
   }

   @Test fun addComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<TestComponent>
         lateinit var componentInDsl: TestComponent

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               componentApplicable = TestComponent {
                  componentInDsl = component
               }
            }
         }

         assertSame(componentInDsl, componentApplicable.component)
         assertSame(rootView.getChildAt(0), componentApplicable.view)
      }
   }

   @Test fun applyComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<TestComponent>

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               componentApplicable = TestComponent {
               }
            }
         }

         rootView.applyKoshian {
            componentApplicable {
               assertSame(componentApplicable.component, component)
            }
         }
      }
   }
}
