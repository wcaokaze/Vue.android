package vue.vuex

import java.util.*
import kotlin.NoSuchElementException

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
         private constructor(val key: Key<*, *, *, *>,
                             val store: VuexStore<*, *, *, *>)
   {
      companion object {
         // class `Module` should not have any type parameter,
         // however it should be checked that the types of Key match the types of VuexStore.
         operator fun <S, M, A, G> invoke(key: Key<S, M, A, G>,
                                          store: VuexStore<S, M, A, G>): Module
               where S : VuexState,
                     M : VuexMutation<S>,
                     A : VuexAction<S, M, G>,
                     G : VuexGetter<S>
         {
            return Module(key, store)
         }
      }

      class Key<S, M, A, G>
            where S : VuexState,
                  M : VuexMutation<S>,
                  A : VuexAction<S, M, G>,
                  G : VuexGetter<S>
   }

   inner class ModuleMap(
         internal var rootModule: VuexStore<*, *, *, *>,
         private val moduleMap: Map<Module.Key<*, *, *, *>, VuexStore<*, *, *, *>>
   ) {
      init {
         setRootModule(rootModule)
      }

      operator fun <MS, MM, MA, MG>
            get(key: Module.Key<MS, MM, MA, MG>): VuexStore<MS, MM, MA, MG>
            where MS : VuexState,
                  MM : VuexMutation<MS>,
                  MA : VuexAction<MS, MM, MG>,
                  MG : VuexGetter<MS>
      {
         val module = moduleMap[key] ?: run {
            val storeName = this@VuexStore::class.toString()
            throw NoSuchElementException("$storeName does not have the specified module")
         }

         @Suppress("UNCHECKED_CAST")
         return module as VuexStore<MS, MM, MA, MG>
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
