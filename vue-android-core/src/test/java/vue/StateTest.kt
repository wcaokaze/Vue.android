package vue

import org.junit.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class StateTest {
   @Test fun gettingInitialValue() {
      val component = object : VueComponent {
         val state = State(0)
      }

      assert(component.state.value == 0)
   }
}
