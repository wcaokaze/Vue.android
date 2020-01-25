package vue

interface VBinder<in T> {
   operator fun invoke(reactiveField: ReactiveField<T>)
}
