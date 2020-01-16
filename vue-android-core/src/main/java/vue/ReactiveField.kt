package vue

typealias V<T> = ReactiveField<T>

interface ReactiveField<out T> {
   val `$vueInternal$value`: T
}

val <T> ReactiveField<T>.value: T
   get() = `$vueInternal$value`
