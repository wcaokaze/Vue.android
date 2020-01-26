package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.app.*
import android.view.*
import android.widget.*
import androidx.lifecycle.*

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

   @Test fun unbind_onViewRemoved() {
      activityScenarioRule.scenario.onActivity { activity ->
         val state = StateField(false)
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
      val state = StateField(false)

      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         view.vBind.isVisible(state)
         activity.setContentView(view)
      }

      assertEquals(1, state.observerCount)
      activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)
      assertEquals(0, state.observerCount)
   }
}
