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

import vue.*

inline fun <reified A> VEvent1<*>.filterIsInstance(): VEvent1<A> {
   @Suppress("UNCHECKED_CAST")
   return filter { a -> a is A } as VEvent1<A>
}

inline fun <reified A, reified B>
      VEvent2<*, *>.filterIsInstance(): VEvent2<A, B>
{
   @Suppress("UNCHECKED_CAST")
   return filter { a, b ->
      a is A && b is B
   } as VEvent2<A, B>
}

inline fun <reified A, reified B, reified C>
      VEvent3<*, *, *>.filterIsInstance(): VEvent3<A, B, C>
{
   @Suppress("UNCHECKED_CAST")
   return filter { a, b, c ->
      a is A && b is B && c is C
   } as VEvent3<A, B, C>
}

inline fun <reified A, reified B, reified C, reified D>
      VEvent4<*, *, *, *>.filterIsInstance(): VEvent4<A, B, C, D>
{
   @Suppress("UNCHECKED_CAST")
   return filter { a, b, c, d ->
      a is A && b is B && c is C && d is D
   } as VEvent4<A, B, C, D>
}
