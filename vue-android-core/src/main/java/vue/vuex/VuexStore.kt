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

import java.util.*
import kotlin.NoSuchElementException
import kotlin.reflect.KClass

internal val storeStack = object : ThreadLocal<LinkedList<VuexStore<*, *, *, *>>>() {
   override fun initialValue() = LinkedList<VuexStore<*, *, *, *>>()
}

abstract class VuexStore<S, M, A, G>
      where S : VuexState,
            M : VuexMutation<S>,
            A : VuexAction<S, M, G>,
            G : VuexGetter<S>
{
   protected abstract fun createState():    S
   protected abstract fun createMutation(): M
   protected abstract fun createAction():   A
   protected abstract fun createGetter():   G

   open fun createModules(): List<Module> = emptyList()

   private var isReady = false

   private var _state:    S? = null
   private var _mutation: M? = null
   private var _action:   A? = null
   private var _getter:   G? = null

   private var _modules: ModuleMap? = null

   val state: S get() {
      ready()
      return _state as S
   }

   val mutation: M get() {
      ready()
      return _mutation as M
   }

   val action: A get() {
      ready()
      return _action as A
   }

   val getter: G get() {
      ready()
      return _getter as G
   }

   val modules: ModuleMap get() {
      ready()
      return _modules as ModuleMap
   }

   val rootModule: VuexStore<*, *, *, *>
      get() = modules.rootModule

   class Module
         private constructor(val key: Key<*, *, *, *, *>,
                             val store: VuexStore<*, *, *, *>)
   {
      companion object {
         // class `Module` should not have any type parameter,
         // however it should be checked that the types of Key match the types of VuexStore.
         operator fun <S, M, A, G> invoke(key: Key<*, S, M, A, G>,
                                          store: VuexStore<S, M, A, G>): Module
               where S : VuexState,
                     M : VuexMutation<S>,
                     A : VuexAction<S, M, G>,
                     G : VuexGetter<S>
         {
            return Module(key, store)
         }

         @Suppress("FunctionName", "UNUSED_PARAMETER")
         fun <Store, S, M, A, G> Key(kClass: KClass<Store>): Key<Store, S, M, A, G>
               where Store : VuexStore<S, M, A, G>,
                     S : VuexState,
                     M : VuexMutation<S>,
                     A : VuexAction<S, M, G>,
                     G : VuexGetter<S>
         {
            return Key()
         }
      }

      class Key<Store, S, M, A, G>
            where Store : VuexStore<S, M, A, G>,
                  S : VuexState,
                  M : VuexMutation<S>,
                  A : VuexAction<S, M, G>,
                  G : VuexGetter<S>
   }

   inner class ModuleMap(
         internal var rootModule: VuexStore<*, *, *, *>,
         private val moduleMap: Map<Module.Key<*, *, *, *, *>, VuexStore<*, *, *, *>>
   ) {
      init {
         setRootModule(rootModule)
      }

      operator fun <Store : VuexStore<*, *, *, *>>
            get(key: Module.Key<Store, *, *, *, *>): Store
      {
         val module = moduleMap[key] ?: run {
            val storeName = this@VuexStore::class.toString()
            throw NoSuchElementException("$storeName does not have the specified module")
         }

         @Suppress("UNCHECKED_CAST")
         return module as Store
      }

      internal fun <S, M, A, G>
            getGeneric(key: Module.Key<*, S, M, A, G>): VuexStore<S, M, A, G>
            where S : VuexState,
                  M : VuexMutation<S>,
                  A : VuexAction<S, M, G>,
                  G : VuexGetter<S>
      {
         @Suppress("UNCHECKED_CAST")
         return this[key] as VuexStore<S, M, A, G>
      }

      private fun setRootModule(newRootModule: VuexStore<*, *, *, *>) {
         rootModule = newRootModule

         for (store in moduleMap.values) {
            store.modules.setRootModule(newRootModule)
         }
      }
   }

   private fun ready() {
      if (isReady) { return }

      synchronized (this) {
         if (isReady) { return }
         isReady = true

         val storeStack = storeStack.get()!!

         storeStack.addLast(this)

         _modules = ModuleMap(
               rootModule = this,
               moduleMap = createModules()
                     .asSequence()
                     .map { Pair(it.key, it.store) }
                     .toMap()
         )

         _state    = createState()
         _mutation = createMutation()
         _getter   = createGetter()
         _action   = createAction()

         storeStack.removeLast()
      }
   }
}
