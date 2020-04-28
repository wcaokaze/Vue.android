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

import android.text.*
import android.widget.EditText

val VOnProvider<EditText>.textChanged: VEvent1<Editable>
   get() = VEvent1 { actionDispatcher ->
      substance.addTextChangedListener(object: TextWatcher {
         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

         override fun afterTextChanged(s: Editable) {
            actionDispatcher(s)
         }
      })
   }
