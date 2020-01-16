package vue

import androidx.annotation.*

class State<T>(initialValue: T) : ReactiveField<T> {
   private var observers: Array<((T) -> Unit)?> = arrayOfNulls(2)

   override var observerCount = 0
      private set

   override val `$vueInternal$value`: T
      get() = value

   var value: T = initialValue
      @UiThread
      set(value) {
         field = value
         notifyObservers(value)
      }

   override fun addObserver(observer: (T) -> Unit) {
      if (observerCount >= observers.size) {
         observers = observers.copyOf(newSize = observerCount * 2)
      }

      observers[observerCount++] = observer
   }

   private fun notifyObservers(value: T) {
      val observers = observers
      val observerCount = observerCount

      for (i in 0 until observerCount) {
         observers[i]?.invoke(value)
      }
   }
}
