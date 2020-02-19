package vue.vuex

import androidx.annotation.*
import vue.*

abstract class VuexState {
   @Deprecated("It is meaningless to get VuexState in VuexState. " +
               "You can omit `state`. Or maybe you meant `state()`?",
               ReplaceWith("state(value)"))
   val state: VuexState
      get() = this

   /**
    * [state][vue.state] that can be written only from [VuexMutation]
    */
   fun <T> state(initialValue: T) = StateField(StateImpl(initialValue))

   class StateField<T>
         internal constructor(private val delegate: StateImpl<T>)
         : ReactiveField<T> by delegate
   {
      internal var value: T
         get() = delegate.value
         @UiThread set(value) {
            delegate.value = value
         }
   }

   val modules: ModuleMap

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexState without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<*, *, *, *>

      modules = ModuleMap(store.modules)
   }

   inner class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<MS, MM, MA, MG>): MS
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules[key].state
      }
   }
}
