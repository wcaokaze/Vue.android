package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class GetterTest {
   @Test fun getInitialValue_withNoObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      assert(getter.value == 2)
   }

   @Test fun getChangedValue_withNoObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      state.value = 2
      assert(getter.value == 4)
   }

   @Test fun observer() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      var i = -1
      getter.addObserver { i = it }
      state.value = 2
      assert(i == 4)
   }

   @Test fun connectToUpstream() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      assert(state.observerCount == 0)
      getter.addObserver {}
      assert(state.observerCount == 1)
   }

   @Test fun disconnectFromUpstream() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      val observer: (Int) -> Unit = {}

      getter.addObserver(observer)
      assert(state.observerCount == 1)
      getter.removeObserver(observer)
      assert(state.observerCount == 0)
   }
}
