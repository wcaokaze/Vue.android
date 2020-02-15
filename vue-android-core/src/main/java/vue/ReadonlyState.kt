package vue

import androidx.annotation.*

internal class ReadonlyState<T>(initialValue: T) : ReactiveField<T> {
   private var observers: Array<((T) -> Unit)?> = arrayOfNulls(2)

   override var observerCount = 0
      private set

   @Suppress("OverridingDeprecatedMember")
   override val `$vueInternal$value`: T
      get() = value

   var value: T = initialValue
      @UiThread
      set(value) {
         field = value
         notifyObservers(value)
      }

   override fun addObserver(observer: (T) -> Unit) {
      if (containsObserver(observer)) { return }

      if (observerCount >= observers.size) {
         observers = observers.copyOf(newSize = observerCount * 2)
      }

      observers[observerCount++] = observer
   }

   override fun removeObserver(observer: (T) -> Unit) {
      val observers = observers

      when (observerCount) {
         0 -> return

         1 -> {
            if (observers[0] === observer) {
               observers[0] = null
               observerCount = 0
            }

            return
         }

         else -> {
            var i = 0

            while (true) {
               if (i >= observerCount) { return }
               if (observers[i] === observer) { break }
               i++
            }

            System.arraycopy(observers, i + 1, observers, i, observerCount - i - 1)
            observers[observerCount - 1] = null
            observerCount--
         }
      }
   }

   private fun notifyObservers(value: T) {
      val observers = observers
      val observerCount = observerCount

      for (i in 0 until observerCount) {
         observers[i]?.invoke(value)
      }
   }

   private fun containsObserver(observer: (T) -> Unit): Boolean {
      for (o in observers) {
         if (o === observer) { return true }
      }

      return false
   }
}
