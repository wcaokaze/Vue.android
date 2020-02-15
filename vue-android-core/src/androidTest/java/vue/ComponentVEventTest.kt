package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.view.*

@RunWith(AndroidJUnit4::class)
class ComponentVEventTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun emitManually() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click { onClick() }
         }
      }

      lateinit var component: Component
      var isCalled = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)
               component.onClick { isCalled = true }
               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }

      assertTrue(isCalled)
   }

   @Test fun bindToAnotherVEvent() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(onClick)
         }
      }

      lateinit var component: Component
      var isCalled = false

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)
               component.onClick { isCalled = true }
               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }

      assertTrue(isCalled)
   }
}
