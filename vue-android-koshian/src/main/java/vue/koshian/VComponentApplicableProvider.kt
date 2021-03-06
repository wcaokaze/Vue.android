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
import koshian.*
import vue.*
import vue.vuex.*

/**
 * We can create the component in Koshian DSL.
 * ```kotlin
 * koshian(context) {
 *    FrameLayout {
 *       Component[::FooComponent, fooStore] {
 *       }
 *    }
 * }
 * ```
 *
 * Note that the Component must have `constructor(Context, VuexStore)` or
 * `constructor(Context)`.
 */
val Component: VComponentApplicableProvider get() {
   val context = `$$KoshianInternal`.context
         ?: throw IllegalStateException(
               "Cannot find Context. " +
               "Make sure that this Component is used in Koshian DSL."
         )

   return VComponentApplicableProvider(context)
}

class VComponentApplicableProvider(private val context: Context) {
   /**
    * Use an already instantiated Store.
    */
   operator fun <C : VComponentInterface<*>> get(component: C) = VComponentApplicable(component)

   /**
    * Manually Store injection.
    * ```kotlin
    * koshian(context) {
    *    FrameLayout {
    *       Component[::FooComponent, fooStore] {
    *       }
    *    }
    * }
    * ```
    */
   operator fun <C, S> get(
         componentConstructor: (Context, S) -> C,
         store: S
   ): VComponentApplicable<C>
         where C : VComponentInterface<S>,
               S : VuexStore<*, *, *, *>
   {
      val component = componentConstructor(context, store)
      return VComponentApplicable(component)
   }

   @Deprecated(
         "This Component requires a VuexStore. Specify a VuexStore like `[::Component, store]`",
         level = DeprecationLevel.ERROR)
   operator fun <C, S> get(
         componentConstructor: (Context, S) -> C
   ): VComponentApplicable<C>
         where C : VComponentInterface<S>,
               S : VuexStore<*, *, *, *>
   {
      throw RuntimeException("This Component ($componentConstructor) requires a VuexStore.")
   }

   /**
    * Component which does not require any Store.
    * ```kotlin
    * koshian(context) {
    *    FrameLayout {
    *       Component[::FooComponent] {
    *       }
    *    }
    * }
    * ```
    */
   operator fun <C> get(
         componentConstructor: (Context) -> C
   ): VComponentApplicable<C>
         where C : VComponentInterface<Nothing>
   {
      val component = componentConstructor(context)
      return VComponentApplicable(component)
   }
}
