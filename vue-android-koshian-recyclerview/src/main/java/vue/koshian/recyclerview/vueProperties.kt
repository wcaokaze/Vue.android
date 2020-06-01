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

package vue.koshian.recyclerview

import androidx.recyclerview.widget.*
import kotlinx.coroutines.*
import vue.*
import kotlin.coroutines.*

val RecyclerViewAdapterComponent<*, *>.onScrolled get() = object : VEvent2<Int, Int> {
   override fun invoke(
      coroutineContext: CoroutineContext,
      action: suspend (Int, Int) -> Unit
   ) {
      componentView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
         override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            launch(Dispatchers.Main.immediate + coroutineContext) {
               action(dx, dy)
            }
         }
      })
   }
}
