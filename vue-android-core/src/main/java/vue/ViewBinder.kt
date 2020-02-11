package vue

import android.view.*
import androidx.annotation.*

/**
 * [VBinder] for [View]s
 */
class ViewBinder<V : View, T>(view: V, private val binder: (T) -> Unit) : VBinder<T> {
   private var isBinding = false
   private var boundReactiveField: ReactiveField<T>? = null

   private val onAttachStateChange = object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View?) {
         bind()
      }

      override fun onViewDetachedFromWindow(v: View?) {
         unbind()
      }
   }

   init {
      view.addOnAttachStateChangeListener(onAttachStateChange)
   }

   override fun invoke(reactiveField: ReactiveField<T>) {
      if (isBinding) {
         unbind()
         boundReactiveField = reactiveField
         bind()
      } else {
         boundReactiveField = reactiveField
      }
   }

   override fun invoke(nonReactiveValue: T) {
      unbind()
      binder(nonReactiveValue)
   }

   @UiThread
   private fun bind() {
      val boundReactiveField = boundReactiveField ?: return

      if (isBinding) { return }
      isBinding = true

      boundReactiveField.addObserver(binder)
      binder(boundReactiveField.value)
   }

   @UiThread
   private fun unbind() {
      if (!isBinding) { return }
      isBinding = false

      boundReactiveField?.removeObserver(binder)
   }
}
