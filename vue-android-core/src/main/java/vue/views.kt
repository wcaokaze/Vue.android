package vue

import android.view.*

val VBindProvider<View>.isVisible: VBinder<Boolean> get() = object : VBinder<Boolean> {
   override fun invoke(reactiveField: ReactiveField<Boolean>) {
      reactiveField.addObserver { isVisible ->
         substance.visibility = if (isVisible) {
            View.VISIBLE
         } else {
            View.INVISIBLE
         }
      }

      substance.visibility = if (reactiveField.value) {
         View.VISIBLE
      } else {
         View.INVISIBLE
      }
   }
}
