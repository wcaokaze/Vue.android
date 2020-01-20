package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class GetterTest {
   @Test fun getValue_withNoObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      assert(getter.value == 2)
   }
}
