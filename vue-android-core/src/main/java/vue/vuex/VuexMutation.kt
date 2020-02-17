package vue.vuex

import androidx.annotation.*

abstract class VuexMutation<S> where S : VuexState {
   val state: S

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
   }

   var <T> VuexState.StateField<T>.value: T
      get() = value
      @UiThread set(v) {
         value = v
      }
}
