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

abstract class VuexMutation<S> where S : VuexState {
   val state: S
   val modules: ModuleMap
   val rootModule: VuexMutation<*> get() = modules.rootModule.mutation

   /**
    * shorthand for `modules[key]`.
    *
    * @return the submodule for the specified key
    */
   operator fun <MS, MM, MA, MG>
         get(key: VuexStore.Module.Key<*, MS, MM, MA, MG>): MM
         where MS : VuexState,
               MM : VuexMutation<MS>,
               MA : VuexAction<MS, MM, MG>,
               MG : VuexGetter<MS>
   {
      return modules[key]
   }

   init {
      val storeStack = storeStack.get()

      if (storeStack.isNullOrEmpty()) {
         throw IllegalStateException(
               "No VuexStore is ready. " +
               "Maybe you attempt to instantiate VuexMutation without VuexStore?")
      }

      @Suppress("UNCHECKED_CAST")
      val store = storeStack.last as VuexStore<S, *, *, *>

      state = store.state
      modules = ModuleMap(store.modules)
   }

   class ModuleMap(private val storeModules: VuexStore<*, *, *, *>.ModuleMap) {
      operator fun <MS, MM, MA, MG>
            get(key: VuexStore.Module.Key<*, MS, MM, MA, MG>): MM
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         return storeModules.getGeneric(key).mutation
      }

      internal val rootModule get() = storeModules.rootModule
   }

   var <T> VuexState.StateField<T>.value: T
      get() = value
      @UiThread set(v) {
         value = v
      }
}
