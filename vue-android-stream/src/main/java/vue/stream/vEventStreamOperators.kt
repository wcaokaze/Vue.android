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
