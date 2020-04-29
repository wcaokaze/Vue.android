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

/**
 * convert this Job to a ReactiveField.
 *
 * The ReactiveField indicates `true` while this Job is running,
 * `false` after this Job is completed.
 */
fun Job.toReactiveField(): ReactiveField<Boolean>
      = JobField(StateImpl(true), this)

private class JobField(private val delegate: StateImpl<Boolean>, job:Job)
      : ReactiveField<Boolean> by delegate
{
   init {
      @UseExperimental(ExperimentalCoroutinesApi::class)
      if (job.isCompleted || job.isCancelled) {
         delegate.value = false
      } else {
         GlobalScope.launch(SupervisorJob() + Dispatchers.Main) {
            job.join()
            delegate.value = false
         }
      }
   }
}
