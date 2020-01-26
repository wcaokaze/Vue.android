package vue

import android.view.*

val VBindProvider<View>.isVisible: VBinder<Boolean>
   get() = viewBinder(substance) { view, value ->
      view.visibility = if (value) {
         View.VISIBLE
      } else {
         View.INVISIBLE
      }
   }
