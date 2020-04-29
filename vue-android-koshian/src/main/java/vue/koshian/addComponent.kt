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
import kotlin.contracts.*

/**
 * adds the specified [VComponent], with Koshian.
 */
@ExperimentalContracts
@Suppress("FunctionName")
inline fun <L, C : VComponent> Koshian<ViewManager, *, L, KoshianMode.Creator>.Component(
      vComponent: C,
      creatorAction: ViewCreator<C, L>.() -> Unit
): C {
   contract { callsInPlace(creatorAction, InvocationKind.EXACTLY_ONCE) }
   return VComponent(vComponent, creatorAction)
}

/**
 * adds the specified [VComponent], with Koshian.
 */
@ExperimentalContracts
@Suppress("FunctionName")
inline fun <L, C : VComponent> CreatorParent<L>.VComponent(
      vComponent: C,
      creatorAction: ViewCreator<C, L>.() -> Unit
): C {
   contract { callsInPlace(creatorAction, InvocationKind.EXACTLY_ONCE) }
   `$$ApplierInternal`.invokeViewInKoshian(`$$koshianInternal$view`, vComponent.componentView)
   val koshian = ViewCreator<C, L>(vComponent)
   koshian.creatorAction()
   return vComponent
}

/**
 * If the next View is the specified Component, applies Koshian to it.
 *
 * Otherwise, inserts the specified Component to the current position.
 */
@Suppress("FunctionName")
inline fun <L, C : VComponent, S : KoshianStyle>
      ApplierParent<L, S>.Component(
            vComponent: C,
            applierAction: ViewApplier<C, L, S>.() -> Unit
      )
{
   VComponent(vComponent, applierAction)
}

/**
 * If the next View is the specified Component, applies Koshian to it.
 *
 * Otherwise, inserts the specified Component to the current position.
 */
@Suppress("FunctionName")
inline fun <L, C : VComponent, S : KoshianStyle>
      ApplierParent<L, S>.VComponent(
            vComponent: C,
            applierAction: ViewApplier<C, L, S>.() -> Unit
      )
{
   `$$ApplierInternal`.invokeViewInKoshian(`$$koshianInternal$view`, vComponent.componentView)
   val koshian = ViewApplier<C, L, S>(vComponent)
   koshian.applierAction()
}

inline val <C : VComponent> Koshian<C, *, *, *>.component: C get() {
   @Suppress("UNCHECKED_CAST")
   return `$$koshianInternal$view` as C
}

inline val <L> Koshian<VComponent, L, *, *>.layout: L get() {
   @Suppress("UNCHECKED_CAST")
   return (`$$koshianInternal$view` as VComponent).componentView.layoutParams as L
}
