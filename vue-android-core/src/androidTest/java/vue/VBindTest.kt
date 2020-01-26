package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.app.*
import android.view.*

class VBindTestActivity : Activity()

@RunWith(AndroidJUnit4::class)
class VBindTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<VBindTestActivity>()

   @Test fun bind() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = StateField(false)
         val view = View(activity)
         view.vBind.isVisible(state)
         activity.setContentView(view)

         assertEquals(1, state.observerCount)
      }
   }

   @Test fun bind_initialValue() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = StateField(false)
         val view = View(activity)

         view.visibility = View.VISIBLE
         view.vBind.isVisible(state)
         activity.setContentView(view)

         assertEquals(View.INVISIBLE, view.visibility)
      }
   }
}
