package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class GetterTest {
   @Test fun getInitialValue_withNoObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      assertEquals(2, getter.value)
   }

   @Test fun getInitialValue_withNoObservers_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }
      assertEquals(6, getter2.value)
   }

   @Test fun getChangedValue_withNoObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      state.value = 2
      assertEquals(4, getter.value)
   }

   @Test fun getChangedValue_withNoObservers_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }
      state.value = 2
      assertEquals(12, getter2.value)
   }

   @Test fun observer() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      var i = -1
      getter.addObserver { i = it }
      state.value = 2
      assertEquals(4, i)
   }

   @Test fun observer_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }

      var i = -1
      getter2.addObserver { i = it }
      state.value = 2
      assertEquals(12, i)
   }

   @Test fun getInitialValue_withObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      getter.addObserver {}
      assertEquals(2, getter.value)
   }

   @Test fun getInitialValue_withObservers_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }
      getter2.addObserver {}
      assertEquals(6, getter2.value)
   }

   @Test fun getChangedValue_withObservers() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }
      getter.addObserver {}
      state.value = 2
      assertEquals(4, getter.value)
   }

   @Test fun getChangedValue_withObservers_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }
      getter2.addObserver {}
      state.value = 2
      assertEquals(12, getter2.value)
   }

   @Test fun connectToUpstream() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      assertEquals(0, state.observerCount)
      getter.addObserver {}
      assertEquals(1, state.observerCount)
   }

   @Test fun connectToUpstream_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }

      assertEquals(0, state.observerCount)
      getter2.addObserver {}
      assertEquals(1, state.observerCount)
   }

   @Test fun disconnectFromUpstream() {
      val state = StateField(1)
      val getter = GetterField { state() * 2 }

      val observer: (Int) -> Unit = {}

      getter.addObserver(observer)
      assertEquals(1, state.observerCount)
      getter.removeObserver(observer)
      assertEquals(0, state.observerCount)
   }

   @Test fun disconnectFromUpstream_viaAnotherGetter() {
      val state = StateField(1)
      val getter1 = GetterField { state() * 2 }
      val getter2 = GetterField { getter1() * 3 }

      val observer: (Int) -> Unit = {}

      getter2.addObserver(observer)
      assertEquals(1, state.observerCount)
      getter2.removeObserver(observer)
      assertEquals(0, state.observerCount)
   }
}
