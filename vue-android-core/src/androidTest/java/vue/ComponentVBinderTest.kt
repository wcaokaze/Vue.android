package vue

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.widget.*

class VBinderTestComponent(context: Context) : VComponentInterface {
   override val view: TextView
   override val componentLifecycle = ComponentLifecycle(this)
   val number = vBinder<Int>()

   fun getCurrentNumber() = number()

   init {
      view = TextView(context)
      view.vBind.text { number().toString() }
   }
}

@RunWith(AndroidJUnit4::class)
class ComponentVBinderTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun initialValueNull() {
      lateinit var component: VBinderTestComponent

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(null, component.getCurrentNumber())
            }
   }

   @Test fun bindValue_state() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(0, component.getCurrentNumber())
            }
   }

   @Test fun bindValue_reactivatee() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number { state() + 1 }
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals(1, component.getCurrentNumber())
            }
   }

   @Test fun bindToView() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
   }

   @Test fun bindToView_vBinder() {
      class VBinderTestComponent(context: Context) : VComponentInterface {
         override val view: TextView
         override val componentLifecycle = ComponentLifecycle(this)
         val text = vBinder<String>()

         init {
            view = TextView(context)
            view.vBind.text(text)
         }
      }

      lateinit var component: VBinderTestComponent
      val state = state("0")

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.text(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = "1"
            }
            .onActivity {
               assertEquals("1", component.view.text)
            }
   }

   @Test fun reactivation() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number(state)
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("1", component.view.text)
            }
   }

   @Test fun reactivation_withLambda() {
      lateinit var component: VBinderTestComponent
      val state = state(0)

      activityScenarioRule.scenario
            .onActivity { activity ->
               component = VBinderTestComponent(activity)
               component.number { state() * 2 }
               activity.setContentView(component.view)
            }
            .onActivity {
               assertEquals("0", component.view.text)
            }
            .onActivity {
               state.value = 1
            }
            .onActivity {
               assertEquals("2", component.view.text)
            }
   }
}
