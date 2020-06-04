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

import android.graphics.drawable.*
import android.view.*

/**
 * [VISIBLE][View.VISIBLE] if `true`, [INVISIBLE][View.INVISIBLE] if `false`
 *
 * Use [isOccupiable] for [GONE][View.GONE]
 */
val VBindProvider<View>.isVisible: VBinder<Boolean>
   get() = createVBinder(::isVisible) { view, value ->
      view.visibility = if (value) {
         View.VISIBLE
      } else {
         View.INVISIBLE
      }
   }

/**
 * [VISIBLE][View.VISIBLE] if `true`, [GONE][View.GONE] if `false`
 *
 * Use [isVisible] for [INVISIBLE][View.INVISIBLE]
 */
val VBindProvider<View>.isOccupiable: VBinder<Boolean>
   get() = createVBinder(::isOccupiable) { view, value ->
      view.visibility = if (value) {
         View.VISIBLE
      } else {
         View.GONE
      }
   }

val VBindProvider<View>.background: VBinder<Drawable>
   get() = createVBinder(::background) { view, value ->
      view.background = value
   }

val VBindProvider<View>.backgroundColor: VBinder<Int>
   get() = createVBinder(::backgroundColor) { view, value ->
      view.setBackgroundColor(value)
   }

val VOnProvider<View>.click: VEvent0
   get() = VEvent0 { actionDispatcher ->
      substance.setOnClickListener { actionDispatcher() }
   }
