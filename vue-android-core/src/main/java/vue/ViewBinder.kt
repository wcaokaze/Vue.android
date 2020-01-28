package vue

import android.view.*
import androidx.annotation.*

/**
 * [VBinder] for [View]s
 */
abstract class ViewBinder<V : View, T>(val view: V)
      : VBinder<T>, (T) -> Unit
{
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

   final override fun invoke(reactiveField: ReactiveField<T>) {
      if (isBinding) {
         unbind()
         boundReactiveField = reactiveField
         bind()
      } else {
         boundReactiveField = reactiveField
      }
   }

   final override fun invoke(reactivatee: Reactivatee<T>) {
      val reactiveField = GetterField(reactivatee)
      invoke(reactiveField)
   }

   @UiThread
   private fun bind() {
      val boundReactiveField = boundReactiveField ?: return

      if (isBinding) { return }
      isBinding = true

      boundReactiveField.addObserver(this)
      invoke(boundReactiveField.value)
   }

   @UiThread
   private fun unbind() {
      if (!isBinding) { return }
      isBinding = false

      boundReactiveField?.removeObserver(this)
   }
}
