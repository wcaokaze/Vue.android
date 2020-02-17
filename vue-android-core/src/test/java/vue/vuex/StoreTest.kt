package vue.vuex

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

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
}
