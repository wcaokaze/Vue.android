package vue

import androidx.annotation.*

typealias V<T> = ReactiveField<T>

interface ReactiveField<out T> {
   /**
    * The current value of this ReactiveField.
    *
    * Do not get this value. Use [value] instead.
    *
    * Implementations for ReactiveField can ignore the warning about this deprecated.
    * ```kotlin
    * @Suppress("OverridingDeprecatedMember")
    * override val `$vueInternal$value`: T
    * ```
    */
   @Deprecated("Do not get this value. Use value instead", ReplaceWith("value", "vue.*"))
   val `$vueInternal$value`: T

   val observerCount: Int

   /**
    * add an observer for the [value] of this ReactiveField.
    *
    * Observers are not duplicated, meaning that this function will ignore
    * for the same observer twice.
    *
    * Observers are called on the Android UI Thread.
    */
   @UiThread
   fun addObserver(@UiThread observer: (T) -> Unit)

   /**
    * remove an observer which was added via [addObserver].
    *
    * If the specified observer is not added to this ReactiveField,
    * this function do nothing.
    */
   @UiThread
   fun removeObserver(observer: (T) -> Unit)
}

/**
 * The current value of this ReactiveField.
 */
val <T> ReactiveField<T>.value: T
   get() {
      @Suppress("DEPRECATION")
      return `$vueInternal$value`
   }

/**
 * A shorthand for [value].
 * @return The current value of this ReactiveField
 */
operator fun <T> ReactiveField<T>.invoke(): T = value
