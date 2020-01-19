package vue

import androidx.annotation.*

typealias V<T> = ReactiveField<T>

interface ReactiveField<out T> {
   val `$vueInternal$value`: T

   val observerCount: Int

   /**
    * add an observer for the [value] of this ReactiveField.
    *
    * Observers are not duplicated, meaning that this function will ignore
    * for the same observer twice.
    */
   @UiThread
   fun addObserver(observer: (T) -> Unit)

   /**
    * remove an observer which was added via [addObserver].
    */
   @UiThread
   fun removeObserver(observer: (T) -> Unit)
}

val <T> ReactiveField<T>.value: T
   get() = `$vueInternal$value`
