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

class CoroutineScopeTestComponent(context: Context) : VComponent() {
   override val view = View(context)
}

@RunWith(AndroidJUnit4::class)
class ComponentCoroutineScopeTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun inactiveUntilMounted() {
      lateinit var component: CoroutineScopeTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = CoroutineScopeTestComponent(activity)
            }
            .onActivity {
               assertFalse(component.isActive)
            }
   }

   @Test fun activeWhileMounted() {
      lateinit var component: CoroutineScopeTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = CoroutineScopeTestComponent(activity)
               activity.setContentView(component.view)
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
               containerView.addView(component.view)
               activity.setContentView(containerView)
            }
            .onActivity { assertTrue(component.isActive) }
            .onActivity { containerView.removeView(component.view) }
            .onActivity { assertFalse(component.isActive) }
   }

   @Test fun coroutineIsCancelled_VOnInComponent_whenComponentIsUnmounted() {
      class VOnScopeTestComponent(context: Context) : VComponent() {
         override val view = View(context)

         init {
            view.vOn.click {
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
               containerView.addView(component.view)
               activity.setContentView(containerView)
            }
            .onActivity { component.view.performClick() }
            .onActivity { containerView.removeView(component.view) }
   }
}
