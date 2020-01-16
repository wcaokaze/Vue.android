package vue

import org.junit.*
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
}
