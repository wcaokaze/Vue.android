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
   private var observers: Array<((Result<T>) -> Unit)> = emptyArray()

   override val observerCount get() = observers.size

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

      observers += observer
   }

   override fun removeObserver(observer: (Result<T>) -> Unit) {
      val a = observers

      when (a.size) {
         0 -> return

         1 -> {
            if (a[0] === observer) {
               observers = emptyArray()
            }

            return
         }

         else -> {
            var i = 0

            while (true) {
               if (i >= a.size) { return }
               if (a[i] === observer) { break }
               i++
            }

            val newL = a.size - 1
            val newA = arrayOfNulls<(Result<T>) -> Unit>(newL)

            System.arraycopy(a,     0, newA, 0, i)
            System.arraycopy(a, i + 1, newA, i, newL - i)

            @Suppress("UNCHECKED_CAST")
            observers = newA as Array<(Result<T>) -> Unit>
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
