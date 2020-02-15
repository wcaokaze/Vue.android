package vue

import androidx.annotation.*

fun <T> state(initialValue: T) = StateField(initialValue)

class StateField<T>
      internal constructor(private val delegate: ReadonlyState<T>)
      : ReactiveField<T> by delegate
{
   constructor(initialValue: T) : this(ReadonlyState(initialValue))

   var value: T
      get() = delegate.value
      @UiThread set(value) {
         delegate.value = value
      }
}
