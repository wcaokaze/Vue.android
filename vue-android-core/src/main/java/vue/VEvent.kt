package vue

import androidx.annotation.*

interface VEvent0 {
   @UiThread operator fun invoke(action: suspend () -> Unit)
}

interface VEvent1<out A> {
   @UiThread operator fun invoke(action: suspend (A) -> Unit)
}

interface VEvent2<out A, out B> {
   @UiThread operator fun invoke(action: suspend (A, B) -> Unit)
}

interface VEvent3<out A, out B, out C> {
   @UiThread operator fun invoke(action: suspend (A, B, C) -> Unit)
}

interface VEvent4<out A, out B, out C, out D> {
   @UiThread operator fun invoke(action: suspend (A, B, C, D) -> Unit)
}

interface VEvent5<out A, out B, out C, out D, out E> {
   @UiThread operator fun invoke(action: suspend (A, B, C, D, E) -> Unit)
}

interface VEvent6<out A, out B, out C, out D, out E, out F> {
   @UiThread operator fun invoke(action: suspend (A, B, C, D, E, F) -> Unit)
}

interface VEvent7<out A, out B, out C, out D, out E, out F, out G> {
   @UiThread operator fun invoke(action: suspend (A, B, C, D, E, F, G) -> Unit)
}

interface VEvent8<out A, out B, out C, out D, out E, out F, out G, out H> {
   @UiThread operator fun invoke(action: suspend (A, B, C, D, E, F, G, H) -> Unit)
}
