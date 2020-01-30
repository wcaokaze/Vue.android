package vue

import android.view.*
import androidx.annotation.*
import kotlinx.coroutines.*

class VOnProvider<out V : View>(val substance: V) {
   inline fun VEvent0(
         crossinline binder: (actionDispatcher: () -> Unit) -> Unit
   ): VEvent0 = object : VEvent0 {
      override fun invoke(action: suspend () -> Unit) {
         binder {
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action()
            }
         }
      }
   }

   inline fun <A> VEvent1(
         crossinline binder: (actionDispatcher: (A) -> Unit) -> Unit
   ): VEvent1<A> = object : VEvent1<A> {
      override fun invoke(action: suspend (A) -> Unit) {
         binder { a ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a)
            }
         }
      }
   }

   inline fun <A, B> VEvent2(
         crossinline binder: (actionDispatcher: (A, B) -> Unit) -> Unit
   ): VEvent2<A, B> = object : VEvent2<A, B> {
      override fun invoke(action: suspend (A, B) -> Unit) {
         binder { a, b ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b)
            }
         }
      }
   }

   inline fun <A, B, C> VEvent3(
         crossinline binder: (actionDispatcher: (A, B, C) -> Unit) -> Unit
   ): VEvent3<A, B, C> = object : VEvent3<A, B, C> {
      override fun invoke(action: suspend (A, B, C) -> Unit) {
         binder { a, b, c ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c)
            }
         }
      }
   }

   inline fun <A, B, C, D> VEvent4(
         crossinline binder: (actionDispatcher: (A, B, C, D) -> Unit) -> Unit
   ): VEvent4<A, B, C, D> = object : VEvent4<A, B, C, D> {
      override fun invoke(action: suspend (A, B, C, D) -> Unit) {
         binder { a, b, c, d ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c, d)
            }
         }
      }
   }

   inline fun <A, B, C, D, E> VEvent5(
         crossinline binder: (actionDispatcher: (A, B, C, D, E) -> Unit) -> Unit
   ): VEvent5<A, B, C, D, E> = object : VEvent5<A, B, C, D, E> {
      override fun invoke(action: suspend (A, B, C, D, E) -> Unit) {
         binder { a, b, c, d, e ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c, d, e)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F> VEvent6(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F) -> Unit) -> Unit
   ): VEvent6<A, B, C, D, E, F> = object : VEvent6<A, B, C, D, E, F> {
      override fun invoke(action: suspend (A, B, C, D, E, F) -> Unit) {
         binder { a, b, c, d, e, f ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c, d, e, f)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F, G> VEvent7(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F, G) -> Unit) -> Unit
   ): VEvent7<A, B, C, D, E, F, G> = object : VEvent7<A, B, C, D, E, F, G> {
      override fun invoke(action: suspend (A, B, C, D, E, F, G) -> Unit) {
         binder { a, b, c, d, e, f, g ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c, d, e, f, g)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F, G, H> VEvent8(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F, G, H) -> Unit) -> Unit
   ): VEvent8<A, B, C, D, E, F, G, H> = object : VEvent8<A, B, C, D, E, F, G, H> {
      override fun invoke(action: suspend (A, B, C, D, E, F, G, H) -> Unit) {
         binder { a, b, c, d, e, f, g, h ->
            GlobalScope.launch(Dispatchers.Main.immediate) {
               action(a, b, c, d, e, f, g, h)
            }
         }
      }
   }
}

@get:UiThread
val <V : View> V.vOn: VOnProvider<V> get() = VOnProvider(this)
