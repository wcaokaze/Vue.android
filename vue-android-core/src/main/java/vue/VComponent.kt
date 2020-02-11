package vue

import android.view.*
import androidx.annotation.*

/**
 * [Reactivatee] which can also be reactivated by [VComponent.ComponentVBinder].
 *
 * In other words, [ComponentVBinder][VComponent.ComponentVBinder] notifies
 * only ComponentReactivatee, doesn't notify normal [Reactivatee].
 * [ReactiveField] notifies both [Reactivatee] and ComponentReactivatee.
 *
 * @see Reactivatee
 */
typealias ComponentReactivatee<T> = VComponent.ComponentReactivateeScope.() -> T

interface VComponent {
   val view: View

   // ==== ComponentVBinder ====================================================

   /*
    * ComponentVBinder is a VBinder, and also a ReactiveField.
    * But we cannot declare it like `ComponentVBinder<T> : VBinder<T>, ReactiveField<T>`,
    * since, actually ComponentVBinder should be a ReactiveField
    * *only in a Component, should not be a ReactiveField outside a Component.*
    * If ComponentVBinder were a subtype of ReactiveField,
    * VComponent implementations would get ugly.
    *
    *     class FooComponent : VComponent {
    *        private val textVBinder = ComponentVBinder<String>()
    *
    *        // To hide ReactiveField
    *        val text: VBind<T> get() = textVBinder
    *     }
    *
    * This is why we don't make ComponentVBinder a ReactiveField.
    * All ReactiveField-like functions are extension functions.
    *
    *     class FooComponent : VComponent {
    *         // Great. This is not a ReactiveField
    *         val text = ComponentVBinder<String>()
    *
    *         init {
    *             // ComponentVBinder looks like a ReactiveField only in a VComponent
    *             textView.vBind.text { text() }
    *         }
    *     }
    */

   /**
    * [VBinder] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class CounterComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val count = state(0)
    *    val buttonText = vBinder<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             textView {
    *                vBind.text { count().toString() }
    *             }
    *
    *             button {
    *                vBind.text { buttonText() }
    *                vOn.click { count.value++ }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val componentButtonText = state("+")
    * val component = CounterComponent()
    * component.buttonText { componentButtonText() }
    *
    * // of course this is reactive.
    * componentButtonText.value = "increment"
    * ```
    */
   fun <T> vBinder() = ComponentVBinder<T>()

   class ComponentVBinder<T> : VBinder<T> {
      private var boundReactiveField: ReactiveField<T>? = null
      internal val field = StateField<T?>(null)

      private val observer: (T) -> Unit = { field.value = it }

      override fun invoke(reactiveField: ReactiveField<T>) {
         unbind()
         boundReactiveField = reactiveField
         bind()
      }

      override fun invoke(nonReactiveValue: T) {
         unbind()
         observer(nonReactiveValue)
      }

      @UiThread
      private fun bind() {
         val boundReactiveField = boundReactiveField ?: return

         boundReactiveField.addObserver(observer)
         field.value = boundReactiveField.value
      }

      @UiThread
      private fun unbind() {
         boundReactiveField?.removeObserver(observer)
      }
   }

   @UiThread
   operator fun <T> VBinder<T?>.invoke(componentVBinder: ComponentVBinder<T>) {
      val reactiveField = componentVBinder.field
      invoke(reactiveField)
   }

   @UiThread
   operator fun <T> VBinder<T>.invoke(reactivatee: ComponentReactivatee<T>) {
      val reactiveField = GetterField(reactivatee)
      invoke(reactiveField)
   }

   /**
    * The current value of this ComponentVBinder
    */
   val <T> ComponentVBinder<T>.value: T?
      get() = field.value

   /**
    * A shorthand for [value].
    * @return The current value of this ComponentVBinder
    */
   operator fun <T> ComponentVBinder<T>.invoke(): T? = value

   interface ComponentReactivateeScope : ReactivateeScope {
      /**
       * [adds][ReactiveField.addObserver] the current [ComponentReactivatee]
       * as an observer for this ComponentVBinder, and returns the current value
       * of this ComponentVBinder.
       */
      @get:UiThread
      val <T> ComponentVBinder<T>.value: T?

      /**
       * A shorthand for [value].
       *
       * [adds][ReactiveField.addObserver] the current [ComponentReactivatee]
       * as an observer for this ComponentVBinder, and returns the current value
       * of this ComponentVBinder.
       *
       * @return The current value of this ComponentVBinder
       */
      @UiThread
      operator fun <T> ComponentVBinder<T>.invoke(): T? = value
   }
}
