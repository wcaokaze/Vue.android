package vue

import androidx.annotation.*

interface VBinder<in T> {
   @UiThread operator fun invoke(reactiveField: ReactiveField<T>)
   @UiThread operator fun invoke(nonReactiveValue: T)
}

@UiThread
operator fun <T> VBinder<T>.invoke(reactivatee: Reactivatee<T>) {
   val reactiveField = GetterField(reactivatee)
   invoke(reactiveField)
}
