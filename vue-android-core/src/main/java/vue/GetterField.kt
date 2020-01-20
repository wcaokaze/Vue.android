package vue

class GetterField<out T>(private val reactivatee: () -> T) {
   val value: T
      get() = reactivatee()
}
