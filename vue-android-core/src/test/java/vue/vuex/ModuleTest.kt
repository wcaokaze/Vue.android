package vue.vuex

import org.junit.runner.*
import org.junit.runners.*
import kotlin.test.*

@RunWith(JUnit4::class)
class ModuleTest {
   class SuperStore : VuexStore<SuperState, SuperMutation, SuperAction, SuperGetter>() {
      object ModuleKeys {
         val SUBMODULE = Module.Key<SubState, SubMutation, SubAction, SubGetter>()
      }

      override fun createState()    = SuperState()
      override fun createMutation() = SuperMutation()
      override fun createAction()   = SuperAction()
      override fun createGetter()   = SuperGetter()

      override fun createModules() = listOf(
            Module(ModuleKeys.SUBMODULE, SubStore())
      )
   }

   class SuperState    : VuexState()
   class SuperMutation : VuexMutation<SuperState>()
   class SuperAction   : VuexAction<SuperState, SuperMutation, SuperGetter>()
   class SuperGetter   : VuexGetter<SuperState>()

   class SubStore : VuexStore<SubState, SubMutation, SubAction, SubGetter>() {
      override fun createState()    = SubState()
      override fun createMutation() = SubMutation()
      override fun createAction()   = SubAction()
      override fun createGetter()   = SubGetter()
   }

   class SubState    : VuexState()
   class SubMutation : VuexMutation<SubState>()
   class SubAction   : VuexAction<SubState, SubMutation, SubGetter>()
   class SubGetter   : VuexGetter<SubState>()

   @Test fun gettingSubmoduleStore() {
      val superModule = SuperStore()
      val submodule = superModule.modules[SuperStore.ModuleKeys.SUBMODULE]

      assert(submodule is SubStore)
   }

   @Test fun moduleNotFoundException() {
      val submodule = SubStore()

      assertFailsWith<NoSuchElementException>(
            "vue.vuex.ModuleTest\$SubStore does not have the specified module") {

         submodule.modules[SuperStore.ModuleKeys.SUBMODULE]
      }
   }
}
