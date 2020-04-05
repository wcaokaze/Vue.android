package vue

import android.view.*
import kotlinx.coroutines.*
import java.util.*

class ComponentLifecycle(private val component: VComponent) {
   class ListenerSet {
      private val listeners = LinkedList<() -> Unit>()

      operator fun plusAssign(listener: () -> Unit) {
         listeners += listener
      }

      internal fun emit() {
         for (l in listeners) {
            l()
         }
      }
   }

   val onAttachedToActivity   = ListenerSet()
   val onDetachedFromActivity = ListenerSet()

   init {
      GlobalScope.launch(Dispatchers.Main) {
         component.view.addOnAttachStateChangeListener(
               object : View.OnAttachStateChangeListener {
                  override fun onViewAttachedToWindow(v: View?) {
                     onAttachedToActivity.emit()
                  }

                  override fun onViewDetachedFromWindow(v: View?) {
                     onDetachedFromActivity.emit()
                  }
               }
         )
      }
   }
}
