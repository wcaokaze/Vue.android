package vue.vuex

abstract class VuexGetter<S> where S : VuexState {
   val state: S
   val modules: ModuleMap
   val rootModule: VuexGetter<*> get() = modules.rootModule.getter

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexGetter without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<S, *, *, *>

      state = store.state
      modules = ModuleMap(store.modules)
   }

   class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<MS, MM, MA, MG>): MG
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules[key].getter
      }

      internal val rootModule get() = storeModules.rootModule
   }
}
