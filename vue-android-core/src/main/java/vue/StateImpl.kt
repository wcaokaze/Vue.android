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

import androidx.annotation.*

internal class StateImpl<T>(initialValue: T) : ReactiveField<T> {
   private var observers: Array<((Result<T>) -> Unit)?> = arrayOfNulls(2)

   override var observerCount = 0
      private set

   @Suppress("OverridingDeprecatedMember")
   override val `$vueInternal$value`: T
      get() = value

   private var _value: Result<T> = Result.success(initialValue)
      set(value) {
         if (value == field) { return }

         field = value
         notifyObservers(value)
      }

   var value: T
      get() = _value.getOrThrow()
      @UiThread
      set(value) {
         _value = Result.success(value)
      }

   fun setFailure(cause: Throwable) {
      _value = Result.failure(cause)
   }

   override fun addObserver(observer: (Result<T>) -> Unit) {
      if (containsObserver(observer)) { return }

      if (observerCount >= observers.size) {
         observers = observers.copyOf(newSize = observerCount * 2)
      }

      observers[observerCount++] = observer
   }

   override fun removeObserver(observer: (Result<T>) -> Unit) {
      val observers = observers

      when (observerCount) {
         0 -> return

         1 -> {
            if (observers[0] === observer) {
               observers[0] = null
               observerCount = 0
            }

            return
         }

         else -> {
            var i = 0

            while (true) {
               if (i >= observerCount) { return }
               if (observers[i] === observer) { break }
               i++
            }

            System.arraycopy(observers, i + 1, observers, i, observerCount - i - 1)
            observers[observerCount - 1] = null
            observerCount--
         }
      }
   }

   private fun notifyObservers(value: Result<T>) {
      val observers = observers
      val observerCount = observerCount

      for (i in 0 until observerCount) {
         observers[i]?.invoke(value)
      }
   }

   private fun containsObserver(observer: (Result<T>) -> Unit): Boolean {
      for (o in observers) {
         if (o === observer) { return true }
      }

      return false
   }
}
