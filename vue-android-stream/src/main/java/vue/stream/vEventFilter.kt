/*
 * Copyright 2020 wcaokaze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vue.stream

import vue.*
import kotlin.coroutines.*

inline fun <A> VEvent1<A>.filter(
      crossinline filter: suspend (A) -> Boolean
): VEvent1<A> {
   return object : VEvent1<A> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a ->
            if (filter(a)) {
               action(a)
            }
         }
      }
   }
}

inline fun <A, B> VEvent2<A, B>.filter(
      crossinline filter: suspend (A, B) -> Boolean
): VEvent2<A, B> {
   return object : VEvent2<A, B> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b ->
            if (filter(a, b)) {
               action(a, b)
            }
         }
      }
   }
}

inline fun <A, B, C> VEvent3<A, B, C>.filter(
      crossinline filter: suspend (A, B, C) -> Boolean
): VEvent3<A, B, C> {
   return object : VEvent3<A, B, C> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c ->
            if (filter(a, b, c)) {
               action(a, b, c)
            }
         }
      }
   }
}

inline fun <A, B, C, D> VEvent4<A, B, C, D>.filter(
      crossinline filter: suspend (A, B, C, D) -> Boolean
): VEvent4<A, B, C, D> {
   return object : VEvent4<A, B, C, D> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c, d ->
            if (filter(a, b, c, d)) {
               action(a, b, c, d)
            }
         }
      }
   }
}

inline fun <A, B, C, D, E> VEvent5<A, B, C, D, E>.filter(
      crossinline filter: suspend (A, B, C, D, E) -> Boolean
): VEvent5<A, B, C, D, E> {
   return object : VEvent5<A, B, C, D, E> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c, d, e ->
            if (filter(a, b, c, d, e)) {
               action(a, b, c, d, e)
            }
         }
      }
   }
}

inline fun <A, B, C, D, E, F> VEvent6<A, B, C, D, E, F>.filter(
      crossinline filter: suspend (A, B, C, D, E, F) -> Boolean
): VEvent6<A, B, C, D, E, F> {
   return object : VEvent6<A, B, C, D, E, F> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c, d, e, f ->
            if (filter(a, b, c, d, e, f)) {
               action(a, b, c, d, e, f)
            }
         }
      }
   }
}

inline fun <A, B, C, D, E, F, G> VEvent7<A, B, C, D, E, F, G>.filter(
      crossinline filter: suspend (A, B, C, D, E, F, G) -> Boolean
): VEvent7<A, B, C, D, E, F, G> {
   return object : VEvent7<A, B, C, D, E, F, G> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F, G) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c, d, e, f, g ->
            if (filter(a, b, c, d, e, f, g)) {
               action(a, b, c, d, e, f, g)
            }
         }
      }
   }
}

inline fun <A, B, C, D, E, F, G, H> VEvent8<A, B, C, D, E, F, G, H>.filter(
      crossinline filter: suspend (A, B, C, D, E, F, G, H) -> Boolean
): VEvent8<A, B, C, D, E, F, G, H> {
   return object : VEvent8<A, B, C, D, E, F, G, H> {
      override fun invoke(
            coroutineContext: CoroutineContext,
            action: suspend (A, B, C, D, E, F, G, H) -> Unit
      ) {
         this@filter.invoke(coroutineContext) { a, b, c, d, e, f, g, h ->
            if (filter(a, b, c, d, e, f, g, h)) {
               action(a, b, c, d, e, f, g, h)
            }
         }
      }
   }
}
