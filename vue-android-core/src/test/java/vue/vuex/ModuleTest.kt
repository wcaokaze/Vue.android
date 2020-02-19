package vue.vuex

import org.junit.runner.*
import org.junit.runners.*
import kotlin.test.*

@RunWith(JUnit4::class)
class ModuleTest {
   class SuperStore : VuexStore<SuperState, SuperMutation, SuperAction, SuperGetter>() {
      object ModuleKeys {
         val SUB_MODULE = Module.Key<SubState, SubMutation, SubAction, SubGetter>()
      }

      override fun createState()    = SuperState()
      override fun createMutation() = SuperMutation()
      override fun createAction()   = SuperAction()
      override fun createGetter()   = SuperGetter()

      override fun createModules() = listOf(
            Module(ModuleKeys.SUB_MODULE, SubStore())
      )
   }

   class SuperState    : VuexState()
   class SuperMutation : VuexMutation<SuperState>()
   class SuperAction   : VuexAction<SuperState, SuperMutation, SuperGetter>()
   class SuperGetter   : VuexGetter<SuperState>()

   class SubStore : VuexStore<SubState, SubMutation, SubAction, SubGetter>() {
      object ModuleKeys {
         val GRAND_SUBMODULE = Module.Key<GrandSubState, GrandSubMutation, GrandSubAction, GrandSubGetter>()
      }

      override fun createState()    = SubState()
      override fun createMutation() = SubMutation()
      override fun createAction()   = SubAction()
      override fun createGetter()   = SubGetter()

      override fun createModules() = listOf(
            Module(ModuleKeys.GRAND_SUBMODULE, GrandSubStore())
      )
   }

   class SubState    : VuexState()
   class SubMutation : VuexMutation<SubState>()
   class SubAction   : VuexAction<SubState, SubMutation, SubGetter>()
   class SubGetter   : VuexGetter<SubState>()

   class GrandSubStore : VuexStore<GrandSubState, GrandSubMutation, GrandSubAction, GrandSubGetter>() {
      override fun createState()    = GrandSubState()
      override fun createMutation() = GrandSubMutation()
      override fun createAction()   = GrandSubAction()
      override fun createGetter()   = GrandSubGetter()
   }

   class GrandSubState    : VuexState()
   class GrandSubMutation : VuexMutation<GrandSubState>()
   class GrandSubAction   : VuexAction<GrandSubState, GrandSubMutation, GrandSubGetter>()
   class GrandSubGetter   : VuexGetter<GrandSubState>()

   @Test fun gettingSubmoduleStore() {
      val superModule = SuperStore()
      val submodule = superModule.modules[SuperStore.ModuleKeys.SUB_MODULE]

      assert(submodule is SubStore)
   }

   @Test fun moduleNotFoundException() {
      val submodule = SubStore()

      val exception = assertFailsWith<NoSuchElementException> {
         submodule.modules[SuperStore.ModuleKeys.SUB_MODULE]
      }

      val message = exception.message
      assertNotNull(message)
      assert("SubStore" in message)
   }

   @Test fun gettingSubmoduleFromState() {
      val superStore = SuperStore()
      val superState = superStore.state

      val subState = superState.modules[SuperStore.ModuleKeys.SUB_MODULE]
      assertSame(superStore.modules[SuperStore.ModuleKeys.SUB_MODULE].state, subState)
   }

   @Test fun moduleFromStateNotFoundException() {
      val subStore = SubStore()
      val subState = subStore.state

      val exception = assertFailsWith<NoSuchElementException> {
         subState.modules[SuperStore.ModuleKeys.SUB_MODULE]
      }

      val message = exception.message
      assertNotNull(message)
      assert("SubStore" in message)
   }

   @Test fun gettingSubmoduleFromMutation() {
      val superStore = SuperStore()
      val superMutation = superStore.mutation

      val subMutation = superMutation.modules[SuperStore.ModuleKeys.SUB_MODULE]
      assertSame(superStore.modules[SuperStore.ModuleKeys.SUB_MODULE].mutation, subMutation)
   }

   @Test fun moduleFromMutationNotFoundException() {
      val subStore = SubStore()
      val subMutation = subStore.mutation

      val exception = assertFailsWith<NoSuchElementException> {
         subMutation.modules[SuperStore.ModuleKeys.SUB_MODULE]
      }

      val message = exception.message
      assertNotNull(message)
      assert("SubStore" in message)
   }

   @Test fun gettingSubmoduleFromAction() {
      val superStore = SuperStore()
      val superAction = superStore.action

      val subAction = superAction.modules[SuperStore.ModuleKeys.SUB_MODULE]
      assertSame(superStore.modules[SuperStore.ModuleKeys.SUB_MODULE].action, subAction)
   }

   @Test fun moduleFromActionNotFoundException() {
      val subStore = SubStore()
      val subAction = subStore.action

      val exception = assertFailsWith<NoSuchElementException> {
         subAction.modules[SuperStore.ModuleKeys.SUB_MODULE]
      }

      val message = exception.message
      assertNotNull(message)
      assert("SubStore" in message)
   }

   @Test fun gettingSubmoduleFromGetter() {
      val superStore = SuperStore()
      val superGetter = superStore.getter

      val subGetter = superGetter.modules[SuperStore.ModuleKeys.SUB_MODULE]
      assertSame(superStore.modules[SuperStore.ModuleKeys.SUB_MODULE].getter, subGetter)
   }

   @Test fun moduleFromGetterNotFoundException() {
      val subStore = SubStore()
      val subGetter = subStore.getter

      val exception = assertFailsWith<NoSuchElementException> {
         subGetter.modules[SuperStore.ModuleKeys.SUB_MODULE]
      }

      val message = exception.message
      assertNotNull(message)
      assert("SubStore" in message)
   }

   @Test fun gettingRootModuleFromRootModule() {
      val superStore = SuperStore()
      assertSame(superStore, superStore.rootModule)
   }

   @Test fun gettingRootModuleFromSubModule() {
      val superStore = SuperStore()
      val subStore = superStore.modules[SuperStore.ModuleKeys.SUB_MODULE]
      assertSame(superStore, subStore.rootModule)
   }

   @Test fun gettingRootModuleFromGrandSubModule() {
      val superStore = SuperStore()
      val subStore = superStore.modules[SuperStore.ModuleKeys.SUB_MODULE]
      val grandSubStore = subStore.modules[SubStore.ModuleKeys.GRAND_SUBMODULE]
      assertSame(superStore, grandSubStore.rootModule)
   }
}
