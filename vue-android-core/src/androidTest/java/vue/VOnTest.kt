package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.view.*
import kotlinx.coroutines.*

@RunWith(AndroidJUnit4::class)
class VOnTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

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

   // No assertions, always passes. This test is only to test compilability.
   @Test fun actionCanCallSuspendFun() {
      activityScenarioRule.scenario.onActivity { activity ->
         val view = View(activity)
         view.vOn.click { delay(1L) }
         activity.setContentView(view)
      }
   }

   @Test fun actionIsCalledOnMainThread() {
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }

   @Test fun coroutineContext() {
      val job = Job()
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(job) {
                  delay(100L)
                  fail("The action is not cancelled")
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
            .onActivity {
               job.cancel()
            }

      Thread.sleep(200L)
   }

   @Test fun actionIsCalledOnMainThread_evenIfCoroutineContextSpecified() {
      val job = Job()
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(job) {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }

   @Test fun dispatcherCanBeOverwritten() {
      lateinit var view: View

      activityScenarioRule.scenario
            .onActivity { activity ->
               view = View(activity)

               view.vOn.click(Dispatchers.Default) {
                  assertFalse(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(view)
            }
            .onActivity {
               view.performClick()
            }
   }
}
