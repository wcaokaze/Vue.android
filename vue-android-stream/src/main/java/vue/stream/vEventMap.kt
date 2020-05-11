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

inline fun <A, R> VEvent1<A>.map(
      crossinline mapper: suspend (A) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a ->
            val mapped = mapper(a)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, R> VEvent2<A, B>.map(
      crossinline mapper: suspend (A, B) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b ->
            val mapped = mapper(a, b)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, R> VEvent3<A, B, C>.map(
      crossinline mapper: suspend (A, B, C) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c ->
            val mapped = mapper(a, b, c)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, D, R> VEvent4<A, B, C, D>.map(
      crossinline mapper: suspend (A, B, C, D) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c, d ->
            val mapped = mapper(a, b, c, d)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, D, E, R> VEvent5<A, B, C, D, E>.map(
      crossinline mapper: suspend (A, B, C, D, E) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c, d, e ->
            val mapped = mapper(a, b, c, d, e)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, D, E, F, R> VEvent6<A, B, C, D, E, F>.map(
      crossinline mapper: suspend (A, B, C, D, E, F) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c, d, e, f ->
            val mapped = mapper(a, b, c, d, e, f)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, D, E, F, G, R> VEvent7<A, B, C, D, E, F, G>.map(
      crossinline mapper: suspend (A, B, C, D, E, F, G) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c, d, e, f, g ->
            val mapped = mapper(a, b, c, d, e, f, g)
            action(mapped)
         }
      }
   }
}

inline fun <A, B, C, D, E, F, G, H, R> VEvent8<A, B, C, D, E, F, G, H>.map(
      crossinline mapper: suspend (A, B, C, D, E, F, G, H) -> R
): VEvent1<R> {
   return object : VEvent1<R> {
      override fun invoke(coroutineContext: CoroutineContext, action: suspend (R) -> Unit) {
         this@map.invoke(coroutineContext) { a, b, c, d, e, f, g, h ->
            val mapped = mapper(a, b, c, d, e, f, g, h)
            action(mapped)
         }
      }
   }
}
