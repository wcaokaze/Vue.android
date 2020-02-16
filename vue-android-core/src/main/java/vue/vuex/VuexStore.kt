package vue.vuex

import java.util.*

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

   private var isReady = false

   private var _state:    S? = null
   private var _mutation: M? = null
   private var _action:   A? = null
   private var _getter:   G? = null

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

   private fun ready() {
      if (isReady) { return }

      synchronized (this) {
         if (isReady) { return }
         isReady = true

         val storeStack = storeStack.get()!!

         storeStack.addLast(this)
         _state    = createState()
         _mutation = createMutation()
         _getter   = createGetter()
         _action   = createAction()
         storeStack.removeLast()
      }
   }
}
