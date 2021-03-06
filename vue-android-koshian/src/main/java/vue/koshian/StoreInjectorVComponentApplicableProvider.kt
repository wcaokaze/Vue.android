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
val <S> VComponentInterface<S>.Component: StoreInjectorVComponentApplicableProvider<S>
      where S : VuexStore<*, *, *, *>
   get() {
      val context = `$$KoshianInternal`.context
            ?: throw IllegalStateException(
                  "Cannot find Context. " +
                  "Make sure that this Component is used in Koshian DSL."
            )

      return StoreInjectorVComponentApplicableProvider(this, context)
   }

class StoreInjectorVComponentApplicableProvider<P>
      (private val parent: VComponentInterface<P>, private val context: Context)
      where P : VuexStore<*, *, *, *>
{
   /**
    * Use an already instantiated Store.
    */
   operator fun <C : VComponentInterface<*>> get(component: C) = VComponentApplicable(component)

   @JvmName("getComponentApplicableWithIllegalStore")
   @Deprecated(
         "Store type mismatch. Check the type of Store for the Component.",
         level = DeprecationLevel.ERROR)
   operator fun <C> get(
         componentConstructor: (Context, VuexStore<*, *, *, *>) -> C,
         store: VuexStore<*, *, *, *>
   ): VComponentApplicable<C>
         where C : VComponentInterface<*>
   {
      throw RuntimeException(
            "The Store ($store) cannot conform to this Component ($componentConstructor).")
   }

   @Deprecated(
         "The Component does not require any VuexStore. Remove the Store.",
         level = DeprecationLevel.ERROR)
   operator fun <C> get(
         componentConstructor: (Context) -> C,
         @Suppress("UNUSED_PARAMETER") store: VuexStore<*, *, *, *>
   ): VComponentApplicable<C>
         where C : VComponentInterface<Nothing>
   {
      val component = componentConstructor(context)
      return VComponentApplicable(component)
   }

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

   @JvmName("getComponentApplicableMissingStore")
   @Deprecated(
         "This Component requires a VuexStore, and cannot inherit Store from parent Component. " +
         "Specify a VuexStore like `[::Component, store]` " +
         "or inject a submodule from parent Component like `[::Component, MODULE_KEY]`",
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

   /**
    * Inherit Store from parent VComponent.
    * ```kotlin
    * class ParentComponent(context: Context, override val store: FooStore) : VComponent<FooStore>() {
    *    override val componentView: FrameLayout
    *
    *    init {
    *       koshian(context) {
    *          componentView = FrameLayout {
    *             Component[::FooComponent] {
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    */
   operator fun <C> get(
         componentConstructor: (Context, P) -> C
   ): VComponentApplicable<C>
         where C : VComponentInterface<P>
   {
      val component = componentConstructor(context, parent.store)
      return VComponentApplicable(component)
   }

   @JvmName("getComponentApplicableWithMismatchedKey")
   @Deprecated(
         "Module Key type mismatch. " +
         "Check the type of Module Key and the type of Store for the Component.",
         level = DeprecationLevel.ERROR)
   operator fun <C, S, K> get(
         componentConstructor: (Context, S) -> C,
         moduleKey: K
   ): VComponentApplicable<C>
         where C : VComponentInterface<S>,
               S : VuexStore<*, *, *, *>,
               K : VuexStore.Module.Key<*, *, *, *, *>
   {
      throw RuntimeException(
            "The specified ModuleKey ($moduleKey) cannot conform to this Component ($componentConstructor).")
   }

   @Deprecated(
         "The Component does not require any VuexStore. Remove the Module Key.",
         level = DeprecationLevel.ERROR)
   operator fun <C, K> get(
         componentConstructor: (Context) -> C,
         @Suppress("UNUSED_PARAMETER") moduleKey: K
   ): VComponentApplicable<C>
         where C : VComponentInterface<Nothing>,
               K : VuexStore.Module.Key<*, *, *, *, *>
   {
      val component = componentConstructor(context)
      return VComponentApplicable(component)
   }

   /**
    * Inject a submodule of the Store that parent VComponent has.
    * ```kotlin
    * class ParentComponent(context: Context, override val store: ParentStore) : VComponent<ParentStore>() {
    *    override val componentView: FrameLayout
    *
    *    init {
    *       koshian(context) {
    *          componentView = FrameLayout {
    *             Component[::FooComponent, ParentStore.ModuleKeys.FOO] {
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    */
   operator fun <C, S> get(
         componentConstructor: (Context, S) -> C,
         moduleKey: VuexStore.Module.Key<S, *, *, *, *>
   ): VComponentApplicable<C>
         where C : VComponentInterface<S>,
               S : VuexStore<*, *, *, *>
   {
      val store = parent.store.modules[moduleKey]
      val component = componentConstructor(context, store)
      return VComponentApplicable(component)
   }
}
