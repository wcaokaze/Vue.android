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

import android.graphics.*
import android.graphics.drawable.*
import android.widget.*

val VBindProvider<ImageView>.image: VBinder<Drawable?>
   get() = createVBinder(::image) { view, value -> view.setImageDrawable(value) }

val VBindProvider<ImageView>.imageBitmap: VBinder<Bitmap?>
   get() = createVBinder(::imageBitmap) { view, value ->
      if (value != null) {
         view.setImageBitmap(value)
      } else {
         view.setImageDrawable(null)
      }
   }
