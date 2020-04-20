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

import kotlinx.coroutines.*

fun <T> Deferred<T>.toReactiveField(): ReactiveField<T?>
      = DeferredField(StateImpl<T?>(null), this)

private class DeferredField<out T>(private val delegate: StateImpl<T?>,
                                   deferred: Deferred<T>)
      : ReactiveField<T?> by delegate
{
   init {
      @UseExperimental(ExperimentalCoroutinesApi::class)
      when {
         deferred.isCompleted -> {
            delegate.value = deferred.getCompleted()
         }

         deferred.isCancelled -> {
            delegate.setFailure(
                  deferred.getCompletionExceptionOrNull() ?: CancellationException())
         }

         else -> {
            GlobalScope.launch(SupervisorJob() + Dispatchers.Main) {
               try {
                  delegate.value = deferred.await()
               } catch (e: Throwable) {
                  delegate.setFailure(e)
               }
            }
         }
      }
   }
}
