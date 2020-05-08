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

package vue

import android.view.*
import androidx.annotation.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.*

class ComponentLifecycle(private val component: VComponentInterface) {
   class ListenerSet {
      private val listeners = LinkedList<() -> Unit>()

      @UiThread
      operator fun plusAssign(listener: () -> Unit) {
         listeners += listener
      }

      @UiThread
      operator fun minusAssign(listener: () -> Unit) {
         listeners -= listener
      }

      internal fun emit() {
         for (l in listeners) {
            l()
         }
      }
   }

   private var job = Job()

   val onAttachedToActivity   = ListenerSet()
   val onDetachedFromActivity = ListenerSet()

   @Transient
   var coroutineContext: CoroutineContext = SupervisorJob(job) + Dispatchers.Main
      private set

   init {
      GlobalScope.launch(Dispatchers.Main) {
         val view = component.componentView

         if (view.isAttachedToWindow) {
            // already attached.
            // It is too late to addOnAttachStateChangeListener,
            // onViewAttachedToWindow will not be called.
            // We have to emit onAttachToActivity manually.
            onAttachedToActivity.emit()
         }

         view.addOnAttachStateChangeListener(
               object : View.OnAttachStateChangeListener {
                  override fun onViewAttachedToWindow(v: View?) {
                     readyScope()
                     onAttachedToActivity.emit()
                  }

                  override fun onViewDetachedFromWindow(v: View?) {
                     releaseScope()
                     onDetachedFromActivity.emit()
                  }
               }
         )
      }
   }

   @UiThread
   private fun readyScope() {
      if (job.isActive) { return }

      job = Job()
      coroutineContext = SupervisorJob(job) + Dispatchers.Main
   }

   @UiThread
   private fun releaseScope() {
      job.cancel()
   }
}
