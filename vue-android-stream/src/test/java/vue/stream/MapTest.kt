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
class MapTest {
   @Test fun getManually() {
      val state = state(0)
      val mapped = state.map { it * 2 }

      assertEquals(0, mapped.value)
      state.value = 1
      assertEquals(2, mapped.value)
   }

   @Test fun reactivation() {
      val state = state(0)
      val mapped = state.map { it * 2 }

      var i = -1
      mapped.addObserver { i = it.getOrThrow() }
      state.value = 1

      assertEquals(2, i)
   }

   @Test fun upstreamFailed() {
      val getter = getter<Int> { throw Exception("Exception from upstream") }
      val mapped = getter.map { it * 2 }

      val exception = assertFails {
         mapped.value
      }

      assertEquals("Exception from upstream", exception.message)
   }

   @Test fun mapperFailed() {
      val state = state(0)
      val mapped: V<Int> = state.map { throw Exception("Exception from mapper") }

      val exception = assertFails {
         mapped.value
      }

      assertEquals("Exception from mapper", exception.message)
   }

   @Test fun resumeFromFail() {
      val state = state(0)

      val mapped = state.map {
         if (it == 1) { throw Exception() }
         it * 2
      }

      state.value = 1
      assertFails { mapped.value }

      state.value = 2
      assertEquals(4, mapped.value)
   }
}
