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

package vue.vuex

import kotlinx.coroutines.*
import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*
import vue.*

@RunWith(JUnit4::class)
class StoreTest {
   class TestStore : VuexStore<TestState, TestMutation, TestAction, TestGetter>() {
      override fun createState()    = TestState()
      override fun createMutation() = TestMutation()
      override fun createAction()   = TestAction()
      override fun createGetter()   = TestGetter()
   }

   class TestState    : VuexState()
   class TestMutation : VuexMutation<TestState>()
   class TestAction   : VuexAction<TestState, TestMutation, TestGetter>()
   class TestGetter   : VuexGetter<TestState>()

   @Test fun createVuexThings() {
      val store = TestStore()
      val state    = store.state
      val mutation = store.mutation
      val action   = store.action
      val getter   = store.getter

      assertSame(state,    store.state)
      assertSame(mutation, store.mutation)
      assertSame(action,   store.action)
      assertSame(getter,   store.getter)
   }

   @Test fun gettingStateFromMutation() {
      val store = TestStore()
      val state    = store.state
      val mutation = store.mutation

      assertSame(state, mutation.state)
   }

   @Test fun mutationCannotInstantiateWithoutStore() {
      assertFailsWith<IllegalStateException> {
         TestMutation()
      }
   }

   @Test fun gettingStateFromGetter() {
      val store = TestStore()
      val state  = store.state
      val getter = store.getter

      assertSame(state, getter.state)
   }

   @Test fun getterCannotInstantiateWithoutStore() {
      assertFailsWith<IllegalStateException> {
         TestGetter()
      }
   }

   @Test fun gettingStateFromAction() {
      val store = TestStore()
      val state  = store.state
      val action = store.action

      assertSame(state, action.state)
   }

   @Test fun gettingMutationFromAction() {
      val store = TestStore()
      val mutation = store.mutation
      val action   = store.action

      assertSame(mutation, action.mutation)
   }

   @Test fun gettingGetterFromAction() {
      val store = TestStore()
      val getter = store.getter
      val action = store.action

      assertSame(getter, action.getter)
   }

   @Test fun actionCannotInstantiateWithoutStore() {
      assertFailsWith<IllegalStateException> {
         TestAction()
      }
   }

   @Test fun integration() {
      class TestState : VuexState() {
         val count = state(0)
      }

      class TestMutation : VuexMutation<TestState>() {
         fun increment() {
            state.count.value++
         }
      }

      class TestGetter : VuexGetter<TestState>() {
         val conutText: V<String> = getter { state.count().toString() }
      }

      class TestAction : VuexAction<TestState, TestMutation, TestGetter>() {
         suspend fun increment() {
            delay(50L)
            mutation.increment()
         }
      }

      class TestStore : VuexStore<TestState, TestMutation, TestAction, TestGetter>() {
         override fun createState()    = TestState()
         override fun createMutation() = TestMutation()
         override fun createAction()   = TestAction()
         override fun createGetter()   = TestGetter()
      }

      runBlocking {
         val store = TestStore()

         assertEquals("0", store.getter.conutText())
         store.action.increment()
         assertEquals("1", store.getter.conutText())
      }
   }
}
