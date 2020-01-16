package vue

import androidx.annotation.*

typealias V<T> = ReactiveField<T>

interface ReactiveField<out T> {
   val `$vueInternal$value`: T

   @UiThread
   fun addObserver(observer: (T) -> Unit)
}

val <T> ReactiveField<T>.value: T
   get() = `$vueInternal$value`
