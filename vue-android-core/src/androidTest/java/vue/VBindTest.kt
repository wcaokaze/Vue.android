package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.app.*
import android.os.*
import android.view.*

class VBindTestActivity : Activity() {
   lateinit var view: View private set

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      view = View(this)
   }
}

@RunWith(AndroidJUnit4::class)
class VBindTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<VBindTestActivity>()

   @Test
   fun bind_initialValue() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = activity.view
         val state = StateField(false)

         view.visibility = View.VISIBLE
         view.vBind.isVisible(state)
         assertEquals(View.INVISIBLE, view.visibility)
      }
   }
}
