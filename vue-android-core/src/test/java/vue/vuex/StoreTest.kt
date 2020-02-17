package vue.vuex

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

@RunWith(JUnit4::class)
class StoreTest {
   @Test fun createVuexThings() {
      class TestState    : VuexState()
      class TestMutation : VuexMutation<TestState>()
      class TestGetter   : VuexGetter<TestState>()
      class TestAction   : VuexAction<TestState, TestMutation, TestGetter>()

      class TestStore : VuexStore<TestState, TestMutation, TestAction, TestGetter>() {
         override fun createState()    = TestState()
         override fun createMutation() = TestMutation()
         override fun createAction()   = TestAction()
         override fun createGetter()   = TestGetter()
      }

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
      class TestState    : VuexState()
      class TestMutation : VuexMutation<TestState>()
      class TestGetter   : VuexGetter<TestState>()
      class TestAction   : VuexAction<TestState, TestMutation, TestGetter>()

      class TestStore : VuexStore<TestState, TestMutation, TestAction, TestGetter>() {
         override fun createState()    = TestState()
         override fun createMutation() = TestMutation()
         override fun createAction()   = TestAction()
         override fun createGetter()   = TestGetter()
      }

      val store = TestStore()
      val state    = store.state
      val mutation = store.mutation

      assertSame(state, mutation.state)
   }

   @Test fun mutationCannotInstantiateWithoutStore() {
      class TestState : VuexState()
      class TestMutation : VuexMutation<TestState>()

      assertFailsWith<IllegalStateException> {
         TestMutation()
      }
   }
}
