package vue

import android.widget.*

val VBindProvider<TextView>.text: VBinder<CharSequence?>
   get() = createVBinder(::text) { view, value -> view.text = value }
