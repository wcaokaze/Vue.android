package vue

class State<T>(initialValue: T) : ReactiveField<T> {
   override val `$vueInternal$value`: T
      get() = value

   var value: T = initialValue
}
