package vue

import android.view.*

val VBindProvider<View>.isVisible: VBinder<Boolean>
   get() = createVBinder(::isVisible) { view, value ->
      view.visibility = if (value) {
         View.VISIBLE
      } else {
         View.INVISIBLE
      }
   }

val VOnProvider<View>.click: VEvent0
   get() = object : VEvent0 {
      override fun invoke(action: () -> Unit) {
         substance.setOnClickListener { action() }
      }
   }
