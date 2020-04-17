/*
 * Copyright 2020 wcaokaze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vue

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

import kotlin.Result

@RunWith(JUnit4::class)
class StateTest {
   @Test fun gettingInitialValue() {
      val state = state(0)
      assertEquals(0, state.value)
   }

   @Test fun settingValue() {
      val state = state(0)
      state.value = 1
      assertEquals(1, state.value)
   }

   @Test fun observer() {
      val state = state(0)
      var i = -1
      state.addObserver { i = it.getOrThrow() }
      state.value = 1
      assertEquals(1, i)
   }

   @Test fun observerShouldNotCalled_onAddingObserver() {
      val state = state(0)
      state.addObserver { fail("observer should not called on added") }
   }

   @Test fun observerCount_increased() {
      val state = state(0)

      repeat (4) { count ->
         assertEquals(count, state.observerCount)
         state.addObserver(Observer {})
      }
   }

   @Test fun removeObserver() {
      val state = state(0)
      var i = -1
      val observer: (Result<Int>) -> Unit = { i = it.getOrThrow() }
      state.addObserver(observer)
      state.value = 1
      state.removeObserver(observer)
      state.value = 2
      assertEquals(1, i)
   }

   @Test fun removeObserver_fifo() {
      val state = state(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Result<Int>) -> Unit = { i1 = it.getOrThrow() }
      val observer2: (Result<Int>) -> Unit = { i2 = it.getOrThrow() }
      val observer3: (Result<Int>) -> Unit = { i3 = it.getOrThrow() }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer1)
      state.value = 2
      state.removeObserver(observer2)
      state.value = 3
      state.removeObserver(observer3)

      assertEquals(1, i1)
      assertEquals(2, i2)
      assertEquals(3, i3)
   }

   @Test fun removeObserver_lifo() {
      val state = state(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Result<Int>) -> Unit = { i1 = it.getOrThrow() }
      val observer2: (Result<Int>) -> Unit = { i2 = it.getOrThrow() }
      val observer3: (Result<Int>) -> Unit = { i3 = it.getOrThrow() }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer3)
      state.value = 2
      state.removeObserver(observer2)
      state.value = 3
      state.removeObserver(observer1)

      assertEquals(3, i1)
      assertEquals(2, i2)
      assertEquals(1, i3)
   }

   @Test fun removeObserver_random() {
      val state = state(0)

      var i1 = -1
      var i2 = -1
      var i3 = -1
      val observer1: (Result<Int>) -> Unit = { i1 = it.getOrThrow() }
      val observer2: (Result<Int>) -> Unit = { i2 = it.getOrThrow() }
      val observer3: (Result<Int>) -> Unit = { i3 = it.getOrThrow() }

      state.addObserver(observer1)
      state.addObserver(observer2)
      state.addObserver(observer3)

      state.value = 1
      state.removeObserver(observer2)
      state.value = 2
      state.removeObserver(observer3)
      state.value = 3
      state.removeObserver(observer1)

      assertEquals(3, i1)
      assertEquals(1, i2)
      assertEquals(2, i3)
   }

   @Test fun observerCount_decreased_fifo() {
      val state = state(0)

      val observers = (0..3).map { Observer<Result<Int>> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      for (o in observers) {
         assertEquals(count, state.observerCount)
         state.removeObserver(o)
         count--
      }
   }

   @Test fun observerCount_decreased_lifo() {
      val state = state(0)

      val observers = (0..3).map { Observer<Result<Int>> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      for (o in observers.reversed()) {
         assertEquals(count, state.observerCount)
         state.removeObserver(o)
         count--
      }
   }

   @Test fun observerCount_decreased_random() {
      val state = state(0)

      val observers = (0..3).map { Observer<Result<Int>> {} }

      for (o in observers) {
         state.addObserver(o)
      }

      var count = 4

      listOf(2, 3, 1)
            .map { observers[it] }
            .forEach { o ->
               assertEquals(count, state.observerCount)
               state.removeObserver(o)
               count--
            }
   }

   @Test fun addObserver_notDuplicated() {
      val state = state(0)
      val observer: (Result<Int>) -> Unit = {}

      state.addObserver(observer)
      state.addObserver(observer)

      assertEquals(1, state.observerCount)
   }

   @Test fun removeObserver_nopForNonAddedObserver() {
      val state = state(0)
      val observer: (Result<Int>) -> Unit = {}

      state.removeObserver(observer)
      assertEquals(0, state.observerCount)
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
