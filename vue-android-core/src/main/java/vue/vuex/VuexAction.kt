package vue.vuex

abstract class VuexAction<S, M, G>
      where S : VuexState,
            M : VuexMutation<S>,
            G : VuexGetter<S>
{
   val state: S
   val mutation: M
   val getter: G
   val modules: ModuleMap
   val rootModule: VuexAction<*, *, *> get() = modules.rootModule.action

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexAction without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<S, M, *, G>

      state    = store.state
      mutation = store.mutation
      getter   = store.getter
      modules = ModuleMap(store.modules)
   }

   class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<MS, MM, MA, MG>): MA
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules[key].action
      }

      internal val rootModule get() = storeModules.rootModule
   }
}
