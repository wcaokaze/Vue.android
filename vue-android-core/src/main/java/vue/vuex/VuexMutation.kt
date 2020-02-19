package vue.vuex

import androidx.annotation.*

abstract class VuexMutation<S> where S : VuexState {
   val state: S
   val modules: ModuleMap
   val rootModule: VuexMutation<*> get() = modules.rootModule.mutation

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexMutation without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<S, *, *, *>

      state = store.state
      modules = ModuleMap(store.modules)
   }

   class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<MS, MM, MA, MG>): MM
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules[key].mutation
      }

      internal val rootModule get() = storeModules.rootModule
   }

   var <T> VuexState.StateField<T>.value: T
      get() = value
      @UiThread set(v) {
         value = v
      }
}
