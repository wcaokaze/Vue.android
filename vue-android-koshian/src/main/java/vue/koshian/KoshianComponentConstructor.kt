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

package vue.koshian

import android.content.*
import vue.*
import vue.vuex.*

/**
 * @see Component
 */
@Suppress("FunctionName")
inline fun <C, S> KoshianComponentConstructor(
      crossinline instantiator: (context: Context, store: S) -> C
): KoshianComponentConstructor<C, S>
      where C : VComponentInterface<S>,
            S : VuexStore<*, *, *, *>
{
   return object : KoshianComponentConstructor<C, S> {
      override fun instantiate(context: Context, store: S): C {
         return instantiator(context, store)
      }
   }
}

/**
 * @see Component
 */
@Suppress("FunctionName")
inline fun <C> KoshianNoStoreComponentConstructor(crossinline instantiate: (Context) -> C)
      : KoshianNoStoreComponentConstructor<C>
      where C : VComponentInterface<Nothing>
{
   return object : KoshianNoStoreComponentConstructor<C> {
      override fun instantiate(context: Context): C {
         return instantiate(context)
      }
   }
}

/**
 * @see Component
 */
interface KoshianComponentConstructor<out C, S>
      where C : VComponentInterface<S>,
            S : VuexStore<*, *, *, *>
{
   fun instantiate(context: Context, store: S): C
}

/**
 * @see Component
 */
interface KoshianNoStoreComponentConstructor<out C>
      : KoshianComponentConstructor<C, Nothing>
      where C : VComponentInterface<Nothing>
{
   override fun instantiate(context: Context, store: Nothing): C
         = instantiate(context)

   fun instantiate(context: Context): C
}
