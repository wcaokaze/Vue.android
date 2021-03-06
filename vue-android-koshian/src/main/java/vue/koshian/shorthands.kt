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

package vue.koshian

import android.view.*
import koshian.*
import vue.*

inline val <V : View> Koshian<V, *, *, *>.vOn: VOnProvider<V>
   get() = view.vOn

inline val <V : View> Koshian<V, *, *, *>.vBind: VBindProvider<V>
   get() = view.vBind

inline val <V : View> Koshian<V, *, *, *>.vModel: VModelProvider<V>
   get() = view.vModel
