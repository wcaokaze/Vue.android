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
class GetterTest {
   @Test fun getInitialValue_withNoObservers() {
      val state = state(1)
      val getter = getter { state() * 2 }
      assertEquals(2, getter.value)
   }

   @Test fun getInitialValue_failure() {
      val getter = getter<Int> { throw Exception("Exception from getter") }

      val exception = assertFails { getter() }
      val message = exception.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun getInitialValue_withNoObservers_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }
      assertEquals(6, getter2.value)
   }

   @Test fun getInitialValue_failure_withNoObservers_viaAnotherGetter() {
      val getter1 = getter<Int> { throw Exception("Exception from getter") }
      val getter2 = getter { getter1() }

      val exception = assertFails { getter2() }
      val message = exception.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun getChangedValue_withNoObservers() {
      val state = state(1)
      val getter = getter { state() * 2 }
      state.value = 2
      assertEquals(4, getter.value)
   }

   @Test fun getChangedValue_withNoObservers_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }
      state.value = 2
      assertEquals(12, getter2.value)
   }

   @Test fun gettingValue_shouldNotBindToUpstream() {
      val state = state(1)
      val getter = getter { state() * 2 }
      getter.value
      assertEquals(0, state.observerCount)
   }

   @Test fun observer() {
      val state = state(1)
      val getter = getter { state() * 2 }

      var i = -1
      getter.addObserver { i = it.getOrThrow() }
      state.value = 2
      assertEquals(4, i)
   }

   @Test fun observer_failure() {
      val state = state(1)

      val getter = getter<Int> {
         state()
         throw Exception("Exception from getter")
      }

      var r: Result<Int>? = null
      getter.addObserver { r = it }
      state.value = 2

      val result = r
      assertNotNull(result)
      assertTrue(result.isFailure)
      val exception = result.exceptionOrNull()
      val message = exception?.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun observer_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }

      var i = -1
      getter2.addObserver { i = it.getOrThrow() }
      state.value = 2
      assertEquals(12, i)
   }

   @Test fun observer_failure_viaAnotherGetter() {
      val state = state(1)

      val getter1 = getter<Int> {
         state()
         throw Exception("Exception from getter")
      }

      val getter2 = getter { getter1() }

      var r: Result<Int>? = null
      getter2.addObserver { r = it }
      state.value = 2

      val result = r
      assertNotNull(result)
      assertTrue(result.isFailure)
      val exception = result.exceptionOrNull()
      val message = exception?.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun getInitialValue_withObservers() {
      val state = state(1)
      val getter = getter { state() * 2 }
      getter.addObserver {}
      assertEquals(2, getter.value)
   }

   @Test fun getInitialValue_failure_withObservers() {
      val getter = getter<Int> { throw Exception("Exception from getter") }
      getter.addObserver {}

      val exception = assertFails { getter() }
      val message = exception.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun getInitialValue_withObservers_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }
      getter2.addObserver {}
      assertEquals(6, getter2.value)
   }

   @Test fun getInitialValue_failure_withObservers_viaAnotherGetter() {
      val getter1 = getter<Int> { throw Exception("Exception from getter") }
      val getter2 = getter { getter1() }
      getter2.addObserver {}

      val exception = assertFails { getter2() }
      val message = exception.message
      assertNotNull(message)
      assertEquals("Exception from getter", message)
   }

   @Test fun getInitialValue_withObservers_shouldNotCallReactivatee() {
      val state = state(1)

      var shouldFail = false

      val getter = getter {
         if (shouldFail) {
            fail()
         }

         state() * 2
      }

      getter.addObserver {}
      shouldFail = true
      getter()
   }

   @Test fun getChangedValue_withObservers() {
      val state = state(1)
      val getter = getter { state() * 2 }
      getter.addObserver {}
      state.value = 2
      assertEquals(4, getter.value)
   }

   @Test fun getChangedValue_withObservers_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }
      getter2.addObserver {}
      state.value = 2
      assertEquals(12, getter2.value)
   }

   @Test fun connectToUpstream() {
      val state = state(1)
      val getter = getter { state() * 2 }

      assertEquals(0, state.observerCount)
      getter.addObserver {}
      assertEquals(1, state.observerCount)
   }

   @Test fun connectToUpstream_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }

      assertEquals(0, state.observerCount)
      getter2.addObserver {}
      assertEquals(1, state.observerCount)
   }

   @Test fun disconnectFromUpstream() {
      val state = state(1)
      val getter = getter { state() * 2 }

      val observer: (Result<Int>) -> Unit = {}

      getter.addObserver(observer)
      assertEquals(1, state.observerCount)
      getter.removeObserver(observer)
      assertEquals(0, state.observerCount)
   }

   @Test fun disconnectFromUpstream_viaAnotherGetter() {
      val state = state(1)
      val getter1 = getter { state() * 2 }
      val getter2 = getter { getter1() * 3 }

      val observer: (Result<Int>) -> Unit = {}

      getter2.addObserver(observer)
      assertEquals(1, state.observerCount)
      getter2.removeObserver(observer)
      assertEquals(0, state.observerCount)
   }

   @Test fun distinct() {
      val state = state(0)
      val getter = getter { state() * 0 }
      getter.addObserver { fail() }
      state.value = 1
   }

   @Test fun shouldNotObserveUnreachableReactiveField() {
      val state1 = state(1)
      val state2 = state(2)
      val conditionState = state(true)

      val getter = getter {
         if (conditionState()) {
            state1()
         } else {
            state2()
         }
      }

      getter.addObserver {}
      assertEquals(1, state1.observerCount)
      assertEquals(0, state2.observerCount)

      conditionState.value = false
      assertEquals(0, state1.observerCount)
      assertEquals(1, state2.observerCount)
   }
}
