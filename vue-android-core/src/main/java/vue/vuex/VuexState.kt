package vue.vuex

import androidx.annotation.*
import vue.*

abstract class VuexState {
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
}
