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

interface VModel<in I, out O> {
   @UiThread fun bind(input: StateField<out I>, output: StateField<in O>)
}

@UiThread
operator fun <I, O, T>
      VModel<I, O>.invoke(state: StateField<T>)
      where T : I, O : T
{
   bind(state, state)
}
