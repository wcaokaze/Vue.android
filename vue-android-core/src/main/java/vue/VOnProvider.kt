package vue

import android.view.*
import androidx.annotation.*
import kotlin.coroutines.*
import kotlinx.coroutines.*

class VOnProvider<out V : View>(val coroutineScope: CoroutineScope,
                                val substance: V)
{
   inline fun VEvent0(
         crossinline binder: (actionDispatcher: () -> Unit) -> Unit
   ): VEvent0 = object : VEvent0 {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend () -> Unit
      ) {
         binder {
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action()
            }
         }
      }
   }

   inline fun <A> VEvent1(
         crossinline binder: (actionDispatcher: (A) -> Unit) -> Unit
   ): VEvent1<A> = object : VEvent1<A> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A) -> Unit
      ) {
         binder { a ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a)
            }
         }
      }
   }

   inline fun <A, B> VEvent2(
         crossinline binder: (actionDispatcher: (A, B) -> Unit) -> Unit
   ): VEvent2<A, B> = object : VEvent2<A, B> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B) -> Unit
      ) {
         binder { a, b ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b)
            }
         }
      }
   }

   inline fun <A, B, C> VEvent3(
         crossinline binder: (actionDispatcher: (A, B, C) -> Unit) -> Unit
   ): VEvent3<A, B, C> = object : VEvent3<A, B, C> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C) -> Unit
      ) {
         binder { a, b, c ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c)
            }
         }
      }
   }

   inline fun <A, B, C, D> VEvent4(
         crossinline binder: (actionDispatcher: (A, B, C, D) -> Unit) -> Unit
   ): VEvent4<A, B, C, D> = object : VEvent4<A, B, C, D> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D) -> Unit
      ) {
         binder { a, b, c, d ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c, d)
            }
         }
      }
   }

   inline fun <A, B, C, D, E> VEvent5(
         crossinline binder: (actionDispatcher: (A, B, C, D, E) -> Unit) -> Unit
   ): VEvent5<A, B, C, D, E> = object : VEvent5<A, B, C, D, E> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E) -> Unit
      ) {
         binder { a, b, c, d, e ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c, d, e)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F> VEvent6(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F) -> Unit) -> Unit
   ): VEvent6<A, B, C, D, E, F> = object : VEvent6<A, B, C, D, E, F> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F) -> Unit
      ) {
         binder { a, b, c, d, e, f ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c, d, e, f)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F, G> VEvent7(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F, G) -> Unit) -> Unit
   ): VEvent7<A, B, C, D, E, F, G> = object : VEvent7<A, B, C, D, E, F, G> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F, G) -> Unit
      ) {
         binder { a, b, c, d, e, f, g ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c, d, e, f, g)
            }
         }
      }
   }

   inline fun <A, B, C, D, E, F, G, H> VEvent8(
         crossinline binder: (actionDispatcher: (A, B, C, D, E, F, G, H) -> Unit) -> Unit
   ): VEvent8<A, B, C, D, E, F, G, H> = object : VEvent8<A, B, C, D, E, F, G, H> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F, G, H) -> Unit
      ) {
         binder { a, b, c, d, e, f, g, h ->
            coroutineScope.launch(Dispatchers.Main.immediate + coroutineContext) {
               action(a, b, c, d, e, f, g, h)
            }
         }
      }
   }
}

@get:UiThread
val <V : View> V.vOn: VOnProvider<V> get() = VOnProvider(GlobalScope, this)
