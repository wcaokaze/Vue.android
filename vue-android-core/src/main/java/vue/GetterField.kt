package vue

import androidx.annotation.*

/**
 * Function which will be re-invoked when any depending ReactiveField is updated.
 *
 * ```kotlin
 * val user = state<User?>(null)
 *
 * val username: V<String?>
 *    = getter { // this lambda is a Reactivatee.
 *
 *       user()?.toString()
 *       //  ^
 *       //  Getting the value.
 *       //  And now this Reactivatee depends on a ReactiveField 'user'.
 *       //  This Reactivatee will be re-invoked when 'user' is updated.
 *    }
 *
 * val usernameLength = getter { username()?.length ?: 0 }
 *
 * fun someFunction() {
 *    user.value = User(name = "wcaokaze")
 *
 *    assert(username() == "wcaokaze")
 *    assert(usernameLength() == 8)
 * }
 * ```
 */
typealias Reactivatee<T> = ReactivateeScope.() -> T

class ReactivateeScope(private val getterField: GetterField<*>) {
   private val observer = fun (_: Any?) {
      getterField.reactivate()
   }

   /**
    * [adds][ReactiveField.addObserver] the current [Reactivatee] as an observer
    * for this ReactiveField, and returns the current value of this ReactiveField.
    */
   @get:UiThread
   val <T> ReactiveField<T>.value: T get() {
      addObserver(observer)

      @Suppress("DEPRECATION")
      return `$vueInternal$value`
   }

   /**
    * A shorthand for [value].
    *
    * [adds][ReactiveField.addObserver] the current [Reactivatee] as an observer
    * for this ReactiveField, and returns the current value of this ReactiveField.
    *
    * @return The current value of this ReactiveField
    */
   @UiThread
   operator fun <T> ReactiveField<T>.invoke(): T = value
}

class GetterField<out T>(@UiThread internal val reactivatee: Reactivatee<T>)
      : ReactiveField<T>
{
   private var observers: Array<((T) -> Unit)?> = arrayOfNulls(2)

   private val reactivateeScope = ReactivateeScope(this)

   override var observerCount = 0
      private set

   @Suppress("OverridingDeprecatedMember")
   override val `$vueInternal$value`: T
      get() = reactivateeScope.reactivatee()

   @UiThread
   internal fun reactivate() {
      val newValue = reactivateeScope.reactivatee()
      notifyObservers(newValue)
   }

   override fun addObserver(observer: (T) -> Unit) {
      if (containsObserver(observer)) { return }

      reactivateeScope.reactivatee()

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
