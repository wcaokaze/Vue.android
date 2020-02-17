package vue.vuex

abstract class VuexAction<S, M, G>
      where S : VuexState,
            M : VuexMutation<S>,
            G : VuexGetter<S>
{
   val state: S
   val mutation: M
   val getter: G

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
   }
}
