package vue

import android.view.*

class VBindProvider<out V : View>(val substance: V)

val <V : View> V.vBind get() = VBindProvider(this)
