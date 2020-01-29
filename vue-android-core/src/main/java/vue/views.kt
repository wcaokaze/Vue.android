package vue

import android.view.*
import kotlinx.coroutines.*

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
      override fun invoke(action: suspend () -> Unit) {
         substance.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action()
            }
         }
      }
   }
