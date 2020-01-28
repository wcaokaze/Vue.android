package vue

import android.view.*
import androidx.annotation.*
import com.wcaokaze.vue.android.*

class VBindProvider<out V : View>(val substance: V) {
   private var binders = emptyArray<Pair<Any, VBinder<*>>>()

   @UiThread
   inline fun <T> createVBinder(
         key: Any,
         crossinline binderAction: (view: V, value: T) -> Unit
   ): VBinder<T> {
      var b = `$vueInternal$getVBinder`(key)

      if (b != null) {
         @Suppress("UNCHECKED_CAST")
         return b as VBinder<T>
      }

      b = ViewBinder<V, T>(substance) {
         binderAction(substance, it)
      }

      `$vueInternal$setVBinder`(key, b)
      return b
   }

   fun `$vueInternal$getVBinder`(key: Any): VBinder<*>? {
      for ((k, b) in binders) {
         if (k == key) { return b }
      }

      return null
   }

   fun `$vueInternal$setVBinder`(key: Any, vBinder: VBinder<*>) {
      binders += key to vBinder
   }
}

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
