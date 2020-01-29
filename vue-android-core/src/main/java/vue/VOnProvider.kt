package vue

import android.view.*
import androidx.annotation.*

class VOnProvider<out V : View>(val substance: V)

@get:UiThread
val <V : View> V.vOn: VOnProvider<V> get() = VOnProvider(this)
