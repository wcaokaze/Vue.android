package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.widget.*
import androidx.lifecycle.*

class SimpleComponent(context: Context) : VComponent {
   override val componentLifecycle = ComponentLifecycle(this)

   override val view: LinearLayout
   val countView: TextView
   val incrementButton: Button

   val count = state(0)

   private fun increment() {
      count.value++
   }

   init {
      view = LinearLayout(context)

      countView = TextView(context)
      countView.vBind.text { count().toString() }
      view.addView(countView)

      incrementButton = Button(context)
      incrementButton.vOn.click { increment() }
      view.addView(incrementButton)
   }
}

@RunWith(AndroidJUnit4::class)
class SimpleComponentTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun reactivation() {
      lateinit var component: SimpleComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = SimpleComponent(activity)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(0, component.count.value)
               assertEquals("0", component.countView.text.toString())
            }
            .onActivity {
               component.incrementButton.performClick()
            }
            .onActivity {
               assertEquals(1, component.count.value)
               assertEquals("1", component.countView.text.toString())
            }
   }

   @Test fun allObserversAreUnmounted() {
      lateinit var component: SimpleComponent

      activityScenarioRule.scenario.onActivity { activity ->
         component = SimpleComponent(activity)
         activity.setContentView(component.view)
      }

      assertEquals(1, component.count.observerCount)

      activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)

      assertEquals(0, component.count.observerCount)
   }
}
