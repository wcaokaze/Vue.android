package vue

class State<out T>(initialValue: T) : ReactiveField<T> {
   override val `$vueInternal$value`: T = initialValue
}
