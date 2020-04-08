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

package vue.vuex

import androidx.annotation.*
import vue.*

abstract class VuexState {
   @Deprecated("It is meaningless to get VuexState in VuexState. " +
               "You can omit `state`. Or maybe you meant `state()`?",
               ReplaceWith("state(value)"))
   val state: VuexState
      get() = this

   /**
    * [state][vue.state] that can be written only from [VuexMutation]
    */
   fun <T> state(initialValue: T) = StateField(StateImpl(initialValue))

   class StateField<T>
         internal constructor(private val delegate: StateImpl<T>)
         : ReactiveField<T> by delegate
   {
      internal var value: T
         get() = delegate.value
         @UiThread set(value) {
            delegate.value = value
         }
   }

   val modules: ModuleMap
   val rootModule: VuexState get() = modules.rootModule.state

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexState without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<*, *, *, *>

      modules = ModuleMap(store.modules)
   }

   class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<MS, MM, MA, MG>): MS
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules[key].state
      }

      internal val rootModule get() = storeModules.rootModule
   }
}

var <T> VuexState.StateField<T>.value: T
   get() = value
   @Deprecated("VuexState can be written only via VuexMutation", level = DeprecationLevel.ERROR)
   set(_) = throw UnsupportedOperationException()
