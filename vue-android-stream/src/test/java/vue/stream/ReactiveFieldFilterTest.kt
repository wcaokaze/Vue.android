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

package vue.stream

import kotlin.test.*
import org.junit.runner.*
import org.junit.runners.*

import vue.*

@RunWith(JUnit4::class)
class ReactiveFieldFilterTest {
   @Test fun getManually() {
      val state = state(0)
      val filtered = state.filter(0) { it % 2 == 0 }

      assertEquals(0, filtered.value)
      state.value = 1
      assertEquals(0, filtered.value)
      state.value = 2
      assertEquals(2, filtered.value)
   }

   @Test fun initialValue() {
      val state = state(1)
      val filtered = state.filter(0) { it % 2 == 0 }

      assertEquals(0, filtered.value)
   }

   @Test fun reactivation() {
      val state = state(0)
      val filtered = state.filter(0) { it % 2 == 0 }

      var i = -1
      filtered.addObserver { i = it.getOrThrow() }

      state.value = 1
      assertEquals(0, filtered.value)
      state.value = 2
      assertEquals(2, filtered.value)
   }
}
