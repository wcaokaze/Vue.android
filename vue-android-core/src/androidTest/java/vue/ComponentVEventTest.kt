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
            view.vOn.click { onClick.emit() }
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

   @Test fun actionIsCalledOnMainThread() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(onClick)
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }
   }

   @Test fun actionIsCalledOnMainThread_evenIfAnotherDispatcherIsSpecifiedInComponent() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(Dispatchers.Default) { onClick.emit() }
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }
   }

   @Test fun coroutineContext() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(onClick)
         }
      }

      val job = Job()
      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(job) {
                  delay(100L)
                  fail("The action is not cancelled")
               }

               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }
            .onActivity {
               job.cancel()
            }

      Thread.sleep(200L)
   }

   @Test fun actionIsCalledOnMainThread_evenIfCoroutineContextSpecified() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(onClick)
         }
      }

      val job = Job()
      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(job) {
                  assertTrue(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }
   }

   @Test fun dispatcherCanBeOverwritten() {
      class Component(context: Context) : VComponent {
         override val view: View
         val onClick = vEvent0()

         init {
            view = View(context)
            view.vOn.click(onClick)
         }
      }

      lateinit var component: Component

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = Component(activity)

               component.onClick(Dispatchers.Default) {
                  assertFalse(activity.mainLooper.isCurrentThread)
               }

               activity.setContentView(component.view)
            }
            .onActivity {
               component.view.performClick()
            }
   }
}
