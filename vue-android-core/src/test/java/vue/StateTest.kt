package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class StateTest {
   @Test fun gettingInitialValue() {
      val state = StateField(0)
      assert(state.value == 0)
   }

   @Test fun settingValue() {
      val state = StateField(0)
      state.value = 1
      assert(state.value == 1)
   }

   @Test fun observer() {
      val state = StateField(0)
      var i = -1
      state.addObserver { i = it }
      state.value = 1
      assert(i == 1)
   }

   @Test fun observerShouldNotCalled_onAddingObserver() {
      val state = StateField(0)
      state.addObserver { fail("observer should not called on added") }
   }

   @Test fun observerCount_increased() {
      val state = StateField(0)

      repeat (4) { count ->
         assert(state.observerCount == count)
         state.addObserver(Observer {})
      }
   }

   @Test fun removeObserver() {
      val state = StateField(0)
      var i = -1
      val observer: (Int) -> Unit = { i = it }
      state.addObserver(observer)
      state.value = 1
      state.removeObserver(observer)
      state.value = 2
      assert(i == 1)
   }

   @Test fun removeObserver_fifo() {
      val state = StateField(0)

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
      val state = StateField(0)

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
      val state = StateField(0)

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

   @Test fun observerCount_decreased_fifo() {
      val state = StateField(0)

      val observers = (0..3).map { Observer<Int> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      for (o in observers) {
         assert(state.observerCount == count)
         state.removeObserver(o)
         count--
      }
   }

   @Test fun observerCount_decreased_lifo() {
      val state = StateField(0)

      val observers = (0..3).map { Observer<Int> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      for (o in observers.reversed()) {
         assert(state.observerCount == count)
         state.removeObserver(o)
         count--
      }
   }

   @Test fun observerCount_decreased_random() {
      val state = StateField(0)

      val observers = (0..3).map { Observer<Int> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      listOf(2, 3, 1)
            .map { observers[it] }
            .forEach { o ->
               assert(state.observerCount == count)
               state.removeObserver(o)
               count--
            }
   }

   @Test fun addObserver_notDuplicated() {
      val state = StateField(0)
      val observer: (Int) -> Unit = {}

      state.addObserver(observer)
      state.addObserver(observer)

      assert(state.observerCount == 1)
   }

   @Test fun removeObserver_nopForNonAddedObserver() {
      val state = StateField(0)
      val observer: (Int) -> Unit = {}

      state.removeObserver(observer)
      assert(state.observerCount == 0)
   }

   /**
    * To waste kotlin optimizer.
    *
    * In Kotlin, lambda expressions that don't capture any values
    * (i.e. non-closure lambdas) are not instantiated twice.
    *
    * This is a wrapper to create 2 or more instances for the observers.
    */
   private class Observer<T>(private val o: (T) -> Unit) : (T) -> Unit {
      override fun invoke(p1: T) {
         o(p1)
      }
   }
}
