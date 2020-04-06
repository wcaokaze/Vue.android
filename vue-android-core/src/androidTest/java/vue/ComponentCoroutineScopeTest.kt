package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.view.*
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
}
