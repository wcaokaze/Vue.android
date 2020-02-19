package vue

import android.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.*
import kotlin.reflect.*

/**
 * Provides [CoroutineScope] for a [VComponent].
 * ```kotlin
 * class ComponentImpl : VComponent, CoroutineScope {
 *    override val coroutineContext by ComponentScope()
 * }
 * ```
 *
 * This scope has [SupervisorJob] and [Dispatchers.Main]. And
 * when [the view of the Component][VComponent.view] gets removed,
 * this context will be [cancelled][CoroutineContext.cancel].
 */
class ComponentScope {
   private var componentView: View? = null
   private var job = Job().apply { cancel() }
   private var coroutineContext: CoroutineContext = SupervisorJob(job) + Dispatchers.Main

   private val onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View?) {
         readyScope()
      }

      override fun onViewDetachedFromWindow(v: View?) {
         releaseScope()
      }
   }

   operator fun getValue(thisRef: VComponent, property: KProperty<*>): CoroutineContext {
      if (thisRef.view === componentView) { return coroutineContext }

      synchronized (this) {
         val v = thisRef.view

         if (v === componentView) { return coroutineContext }

         componentView?.removeOnAttachStateChangeListener(onAttachStateChangeListener)
         componentView = v
         v.addOnAttachStateChangeListener(onAttachStateChangeListener)

         if (v.isAttachedToWindow) {
            readyScope()
         } else {
            releaseScope()
         }

         return coroutineContext
      }
   }

   private fun readyScope() {
      val newJob = Job()
      job.cancel()
      job = newJob
      coroutineContext = SupervisorJob(newJob) + Dispatchers.Main
   }

   private fun releaseScope() {
      job.cancel()
   }
}
