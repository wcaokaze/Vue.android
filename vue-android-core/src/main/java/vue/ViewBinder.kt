package vue

import android.view.*

inline fun <V : View, T> viewBinder(
      view: V,
      crossinline binderAction: (view: V, value: T) -> Unit
): ViewBinder<V, T> = object : ViewBinder<V, T>(view) {
   override fun invoke(value: T) {
      binderAction(view, value)
   }
}

/**
 * [VBinder] for [View]s
 */
abstract class ViewBinder<V : View, T>(private val view: V)
      : VBinder<T>, (T) -> Unit
{
   private var boundReactiveField: ReactiveField<T>? = null

   private val onAttachStateChange = object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View?) {
      }

      override fun onViewDetachedFromWindow(v: View?) {
         unbind()
      }
   }

   init {
      view.addOnAttachStateChangeListener(onAttachStateChange)
   }

   override fun invoke(reactiveField: ReactiveField<T>) {
      unbind()
      boundReactiveField = reactiveField
      reactiveField.addObserver(this)
      invoke(reactiveField.value)
   }

   private fun unbind() {
      boundReactiveField?.removeObserver(this)
   }
}
