package vue.vuex

import androidx.annotation.*

abstract class VuexMutation<S> where S : VuexState {
   var <T> VuexState.StateField<T>.value: T
      get() = value
      @UiThread set(v) {
         value = v
      }
}
