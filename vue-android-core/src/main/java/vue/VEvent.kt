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

package vue

import androidx.annotation.*
import kotlin.coroutines.*

interface VEvent0 {
   /*
    * The default value is EmptyCoroutineContext, not Dispatchers.Main.
    * Dispatchers.Main should be applied in the function.
    *
    *     // Bad
    *     operator fun invoke(coroutineContext: CoroutineContext = Dispatcher.Main) {
    *        launch(coroutineContext) {
    *        }
    *     }
    *
    *     // Good
    *     operator fun invoke(coroutineContext: CoroutineContext = EmptyCoroutineContext) {
    *        launch(Dispatchers.Main + coroutineContext) {
    *        }
    *     }
    *
    * Since Dispatchers.Main may be removed unintentionally like the follow
    *
    *     event(CoroutineName("name")) {}
    */
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend () -> Unit
   )
}

interface VEvent1<out A> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A) -> Unit
   )
}

interface VEvent2<out A, out B> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B) -> Unit
   )
}

interface VEvent3<out A, out B, out C> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C) -> Unit
   )
}

interface VEvent4<out A, out B, out C, out D> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C, D) -> Unit
   )
}

interface VEvent5<out A, out B, out C, out D, out E> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C, D, E) -> Unit
   )
}

interface VEvent6<out A, out B, out C, out D, out E, out F> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C, D, E, F) -> Unit
   )
}

interface VEvent7<out A, out B, out C, out D, out E, out F, out G> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C, D, E, F, G) -> Unit
   )
}

interface VEvent8<out A, out B, out C, out D, out E, out F, out G, out H> {
   @UiThread
   operator fun invoke(
         coroutineContext: CoroutineContext = EmptyCoroutineContext,
         action: suspend (A, B, C, D, E, F, G, H) -> Unit
   )
}
