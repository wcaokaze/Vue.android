package vue

import androidx.annotation.*

interface VBinder<in T> {
   @UiThread operator fun invoke(reactiveField: ReactiveField<T>)
   @UiThread operator fun invoke(reactivatee: Reactivatee<T>)
}
