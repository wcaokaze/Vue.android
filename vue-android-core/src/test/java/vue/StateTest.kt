package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class StateTest {
   @Test fun gettingInitialValue() {
      val state = State(0)
      assert(state.value == 0)
   }

   @Test fun settingValue() {
      val state = State(0)
      state.value = 1
      assert(state.value == 1)
   }

   @Test fun observer() {
      val state = State(0)
      var i = -1
      state.addObserver { i = it }
      state.value = 1
      assert(i == 1)
   }

   @Test fun observerShouldNotCalled_onAddingObserver() {
      val state = State(0)
      state.addObserver { fail("observer should not called on added") }
   }
}
