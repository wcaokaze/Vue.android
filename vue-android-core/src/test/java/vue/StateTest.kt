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

   @Test fun observerCount_increased() {
      val state = State(0)

      repeat (4) { count ->
         assert(state.observerCount == count)
         state.addObserver {}
      }
   }

   @Test fun removeObserver() {
      val state = State(0)
      var i = -1
      val observer: (Int) -> Unit = { i = it }
      state.addObserver(observer)
      state.value = 1
      state.removeObserver(observer)
      state.value = 2
      assert(i == 1)
   }

   @Test fun removeObserver_fifo() {
      val state = State(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Int) -> Unit = { i1 = it }
      val observer2: (Int) -> Unit = { i2 = it }
      val observer3: (Int) -> Unit = { i3 = it }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer1)
      state.value = 2
      state.removeObserver(observer2)
      state.value = 3
      state.removeObserver(observer3)

      assert(i1 == 1)
      assert(i2 == 2)
      assert(i3 == 3)
   }

   @Test fun removeObserver_lifo() {
      val state = State(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Int) -> Unit = { i1 = it }
      val observer2: (Int) -> Unit = { i2 = it }
      val observer3: (Int) -> Unit = { i3 = it }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer3)
      state.value = 2
      state.removeObserver(observer2)
      state.value = 3
      state.removeObserver(observer1)

      assert(i1 == 3)
      assert(i2 == 2)
      assert(i3 == 1)
   }

   @Test fun removeObserver_random() {
      val state = State(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Int) -> Unit = { i1 = it }
      val observer2: (Int) -> Unit = { i2 = it }
      val observer3: (Int) -> Unit = { i3 = it }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer2)
      state.value = 2
      state.removeObserver(observer3)
      state.value = 3
      state.removeObserver(observer1)

      assert(i1 == 3)
      assert(i2 == 1)
      assert(i3 == 2)
   }
}
