package vue.koshian

import koshian.*
import vue.*
import kotlin.contracts.*

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
   `$$ApplierInternal`.invokeViewInKoshian(`$$koshianInternal$view`, vComponent.view)
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
      ApplierParent<L, S>.VComponent(
            vComponent: C,
            applierAction: ViewApplier<C, L, S>.() -> Unit
      )
{
   `$$ApplierInternal`.invokeViewInKoshian(`$$koshianInternal$view`, vComponent.view)
   val koshian = ViewApplier<C, L, S>(vComponent)
   koshian.applierAction()
}

inline val <C : VComponent> Koshian<C, *, *, *>.component: C get() {
   @Suppress("UNCHECKED_CAST")
   return `$$koshianInternal$view` as C
}

inline val <L> Koshian<VComponent, L, *, *>.layout: L get() {
   @Suppress("UNCHECKED_CAST")
   return (`$$koshianInternal$view` as VComponent).view.layoutParams as L
}
