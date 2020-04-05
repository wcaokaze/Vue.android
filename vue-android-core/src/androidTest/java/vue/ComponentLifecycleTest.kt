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

class LifecycleTestComponent(context: Context) : VComponent {
   override val view = View(context)
   val componentLifecycle = ComponentLifecycle(this)
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
            .onActivity { activity -> activity.setContentView(component.view) }
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
            .onActivity { containerView.addView(component.view) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.removeView(component.view) }
            .onActivity { assertEquals(1, count) }
            .onActivity { containerView.addView(component.view) }
            .onActivity { assertEquals(2, count) }
   }
}
