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
}

var <T> VuexState.StateField<T>.value: T
   get() = value
   @Deprecated("VuexState can be written only via VuexMutation", level = DeprecationLevel.ERROR)
   set(_) = throw UnsupportedOperationException()
