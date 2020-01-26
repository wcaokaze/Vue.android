package vue

import android.view.*
import androidx.annotation.*
import com.wcaokaze.vue.android.*

class VBindProvider<out V : View>(val substance: V)

@get:UiThread
val <V : View> V.vBind: VBindProvider<V> get() {
   val cachedProvider = getTag(R.id.view_tag_v_bind_provider)

   if (cachedProvider != null) {
      @Suppress("UNCHECKED_CAST")
      return cachedProvider as VBindProvider<V>
   }

   val newProvider = VBindProvider(this)
   setTag(R.id.view_tag_v_bind_provider, newProvider)
   return newProvider
}
