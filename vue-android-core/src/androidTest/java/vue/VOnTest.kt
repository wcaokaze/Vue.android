package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.view.*

@RunWith(AndroidJUnit4::class)
class VOnTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<VBindTestActivity>()

   @Test fun bind() {
      var isCalled = false
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)
               view.vOn.click { isCalled = true }
               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }

      assertTrue(isCalled)
   }
}
