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

import android.view.*
import androidx.annotation.*
import kotlinx.coroutines.*
import vue.vuex.*
import kotlin.coroutines.*

/**
 * [Reactivatee] which can also be reactivated by [VComponentInterface.ComponentVBinder].
 *
 * In other words, [ComponentVBinder][VComponentInterface.ComponentVBinder] notifies
 * only ComponentReactivatee, doesn't notify normal [Reactivatee].
 * [ReactiveField] notifies both [Reactivatee] and ComponentReactivatee.
 *
 * @see Reactivatee
 */
typealias ComponentReactivatee<T> = VComponentInterface.ComponentReactivateeScope.() -> T

/**
 * @param S
 *   [VuexStore] which this Component uses.
 *
 *   Basically this should be received on the constructor.
 *   ```kotlin
 *   class ComponentImpl(override val store: Store) : VComponent<Store>()
 *   ```
 *   Or specify [Nothing] if this Component does not require any [VuexStore].
 *   ```kotlin
 *   class ComponentImpl : VComponent<Nothing>() {
 *      override val store: Nothing get() = throw UnsupportedOperationException()
 *   }
 *   ```
 */
abstract class VComponent<S : VuexStore<*, *, *, *>> : VComponentInterface<S> {
   @Suppress("LeakingThis")
   override val componentLifecycle = ComponentLifecycle(this)
}

/**
 * @param S
 *   [VuexStore] which this Component uses.
 *
 *   Basically this should be received on the constructor.
 *   ```kotlin
 *   class ComponentImpl(override val store: Store) : VComponentInterface<Store>
 *   ```
 *   Or specify [Nothing] if this Component does not require any [VuexStore].
 *   ```kotlin
 *   class ComponentImpl : VComponentInterface<Nothing> {
 *      override val store: Nothing get() = throw UnsupportedOperationException()
 *   }
 *   ```
 */
interface VComponentInterface<S : VuexStore<*, *, *, *>> : CoroutineScope {
   val componentView: View

   val componentLifecycle: ComponentLifecycle
   val store: S

   override val coroutineContext: CoroutineContext
      get() = componentLifecycle.coroutineContext

   val <V : View> V.vOn: VOnProvider<V>
      @UiThread get() = VOnProvider(this@VComponentInterface, this)

   // ==== watcher =============================================================

   /**
    * observes a [ReactiveField].
    *
    * watcher invokes [removeObserver][ReactiveField.removeObserver] automatically.
    * You don't have to manage the Component's lifetime.
    */
   fun <T> watcher(watchedReactiveField: ReactiveField<T>, watcher: (T) -> Unit) {
      val observer = fun (r: Result<T>) {
         val v = r.getOrNull() ?: return
         watcher(v)
      }

      componentLifecycle.onAttachedToActivity += {
         watchedReactiveField.addObserver(observer)
      }

      componentLifecycle.onDetachedFromActivity += {
         watchedReactiveField.removeObserver(observer)
      }
   }

   // ==== ReadonlyState =======================================================

   /**
    * [state] that cannot be written from outside a Component.
    */
   fun <T> readonlyState(initialValue: T) = ReadonlyState(StateImpl(initialValue))

   class ReadonlyState<T>
         internal constructor(internal val delegate: StateImpl<T>)
         : ReactiveField<T> by delegate

   var <T> ReadonlyState<T>.value: T
      get() = delegate.value
      set(value) {
         delegate.value = value
      }

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
      internal val field = StateImpl<T?>(null)

      private val observer: (Result<T>) -> Unit = { result ->
         result.onSuccess { field.value = it }
               .onFailure { field.setFailure(it) }
      }

      override fun invoke(reactiveField: ReactiveField<T>) {
         unbind()
         boundReactiveField = reactiveField
         bind()
      }

      override fun invoke(nonReactiveValue: T) {
         unbind()
         observer(Result.success(nonReactiveValue))
      }

      @UiThread
      private fun bind() {
         val boundReactiveField = boundReactiveField ?: return

         boundReactiveField.addObserver(observer)

         try {
            field.value = boundReactiveField.value
         } catch (e: Throwable) {
            field.setFailure(e)
         }
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

   fun <T> getter(reactivatee: ComponentReactivatee<T>) = GetterField(reactivatee)

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

   // ==== ComponentVEvent0 ====================================================

   /**
    * [VEvent0] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun vEvent0() = ComponentVEvent0()

   class ComponentVEvent0 : VEvent0 {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend () -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend () -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch() {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke()
         }
      }
   }

   fun ComponentVEvent0.emit() {
      dispatch()
   }

   operator fun VEvent0.invoke(componentVEvent: ComponentVEvent0) {
      invoke { componentVEvent.dispatch() }
   }

   // ==== ComponentVEvent1 ====================================================

   /**
    * [VEvent1] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A> vEvent1() = ComponentVEvent1<A>()

   class ComponentVEvent1<A> : VEvent1<A> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0)
         }
      }
   }

   fun <A> ComponentVEvent1<A>.emit(arg0: A) {
      dispatch(arg0)
   }

   operator fun <A> VEvent1<A>.invoke(componentVEvent: ComponentVEvent1<A>) {
      invoke { arg0 ->
         componentVEvent.dispatch(arg0)
      }
   }

   // ==== ComponentVEvent2 ====================================================

   /**
    * [VEvent2] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B> vEvent2() = ComponentVEvent2<A, B>()

   class ComponentVEvent2<A, B> : VEvent2<A, B> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1)
         }
      }
   }

   fun <A, B> ComponentVEvent2<A, B>.emit(arg0: A, arg1: B) {
      dispatch(arg0, arg1)
   }

   operator fun <A, B> VEvent2<A, B>.invoke(componentVEvent: ComponentVEvent2<A, B>) {
      invoke { arg0, arg1 ->
         componentVEvent.dispatch(arg0, arg1)
      }
   }

   // ==== ComponentVEvent3 ====================================================

   /**
    * [VEvent3] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C> vEvent3() = ComponentVEvent3<A, B, C>()

   class ComponentVEvent3<A, B, C> : VEvent3<A, B, C> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2)
         }
      }
   }

   fun <A, B, C> ComponentVEvent3<A, B, C>.emit(arg0: A, arg1: B, arg2: C) {
      dispatch(arg0, arg1, arg2)
   }

   operator fun <A, B, C> VEvent3<A, B, C>.invoke(componentVEvent: ComponentVEvent3<A, B, C>) {
      invoke { arg0, arg1, arg2 ->
         componentVEvent.dispatch(arg0, arg1, arg2)
      }
   }

   // ==== ComponentVEvent4 ====================================================

   /**
    * [VEvent4] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C, D> vEvent4() = ComponentVEvent4<A, B, C, D>()

   class ComponentVEvent4<A, B, C, D> : VEvent4<A, B, C, D> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C, D) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C, D) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C, arg3: D) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2, arg3)
         }
      }
   }

   fun <A, B, C, D> ComponentVEvent4<A, B, C, D>.emit(arg0: A, arg1: B, arg2: C, arg3: D) {
      dispatch(arg0, arg1, arg2, arg3)
   }

   operator fun <A, B, C, D> VEvent4<A, B, C, D>.invoke(componentVEvent: ComponentVEvent4<A, B, C, D>) {
      invoke { arg0, arg1, arg2, arg3 ->
         componentVEvent.dispatch(arg0, arg1, arg2, arg3)
      }
   }

   // ==== ComponentVEvent5 ====================================================

   /**
    * [VEvent5] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C, D, E> vEvent5() = ComponentVEvent5<A, B, C, D, E>()

   class ComponentVEvent5<A, B, C, D, E> : VEvent5<A, B, C, D, E> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C, D, E) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C, D, E) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2, arg3, arg4)
         }
      }
   }

   fun <A, B, C, D, E> ComponentVEvent5<A, B, C, D, E>.emit(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E) {
      dispatch(arg0, arg1, arg2, arg3, arg4)
   }

   operator fun <A, B, C, D, E> VEvent5<A, B, C, D, E>.invoke(componentVEvent: ComponentVEvent5<A, B, C, D, E>) {
      invoke { arg0, arg1, arg2, arg3, arg4 ->
         componentVEvent.dispatch(arg0, arg1, arg2, arg3, arg4)
      }
   }

   // ==== ComponentVEvent6 ====================================================

   /**
    * [VEvent6] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C, D, E, F> vEvent6() = ComponentVEvent6<A, B, C, D, E, F>()

   class ComponentVEvent6<A, B, C, D, E, F> : VEvent6<A, B, C, D, E, F> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C, D, E, F) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C, D, E, F) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2, arg3, arg4, arg5)
         }
      }
   }

   fun <A, B, C, D, E, F> ComponentVEvent6<A, B, C, D, E, F>.emit(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F) {
      dispatch(arg0, arg1, arg2, arg3, arg4, arg5)
   }

   operator fun <A, B, C, D, E, F> VEvent6<A, B, C, D, E, F>.invoke(componentVEvent: ComponentVEvent6<A, B, C, D, E, F>) {
      invoke { arg0, arg1, arg2, arg3, arg4, arg5 ->
         componentVEvent.dispatch(arg0, arg1, arg2, arg3, arg4, arg5)
      }
   }

   // ==== ComponentVEvent7 ====================================================

   /**
    * [VEvent7] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C, D, E, F, G> vEvent7() = ComponentVEvent7<A, B, C, D, E, F, G>()

   class ComponentVEvent7<A, B, C, D, E, F, G> : VEvent7<A, B, C, D, E, F, G> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C, D, E, F, G) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C, D, E, F, G) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F, arg6: G) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
         }
      }
   }

   fun <A, B, C, D, E, F, G> ComponentVEvent7<A, B, C, D, E, F, G>.emit(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F, arg6: G) {
      dispatch(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
   }

   operator fun <A, B, C, D, E, F, G> VEvent7<A, B, C, D, E, F, G>.invoke(componentVEvent: ComponentVEvent7<A, B, C, D, E, F, G>) {
      invoke { arg0, arg1, arg2, arg3, arg4, arg5, arg6 ->
         componentVEvent.dispatch(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
      }
   }

   // ==== ComponentVEvent8 ====================================================

   /**
    * [VEvent8] for a VComponent.
    *
    * Here is an example for VComponent.
    * ```kotlin
    * class SearchComponent(context: Context) : VComponent {
    *    override val view: LinearLayout
    *
    *    val onSubmit = vEvent1<String>()
    *
    *    init {
    *       view = koshian(context) {
    *          linearLayout {
    *             val editText = editText {
    *             }
    *
    *             button {
    *                view.text = "Search"
    *
    *                vOn.click {
    *                   val text = editText.text.toString()
    *                   onSubmit(text)
    *                }
    *             }
    *          }
    *       }
    *    }
    * }
    * ```
    *
    * And this is an example for the use site:
    * ```kotlin
    * val component = SearchComponent()
    * component.onSubmit { text -> search(text) }
    * ```
    */
   fun <A, B, C, D, E, F, G, H> vEvent8() = ComponentVEvent8<A, B, C, D, E, F, G, H>()

   class ComponentVEvent8<A, B, C, D, E, F, G, H> : VEvent8<A, B, C, D, E, F, G, H> {
      private var coroutineContext: CoroutineContext = EmptyCoroutineContext
      private var action: (suspend (A, B, C, D, E, F, G, H) -> Unit)? = null

      override fun invoke(coroutineContext: CoroutineContext, action: suspend (A, B, C, D, E, F, G, H) -> Unit) {
         this.coroutineContext = coroutineContext
         this.action = action
      }

      internal fun dispatch(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F, arg6: G, arg7: H) {
         GlobalScope.launch(Dispatchers.Main.immediate + coroutineContext) {
            action?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
         }
      }
   }

   fun <A, B, C, D, E, F, G, H> ComponentVEvent8<A, B, C, D, E, F, G, H>.emit(arg0: A, arg1: B, arg2: C, arg3: D, arg4: E, arg5: F, arg6: G, arg7: H) {
      dispatch(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
   }

   operator fun <A, B, C, D, E, F, G, H> VEvent8<A, B, C, D, E, F, G, H>.invoke(componentVEvent: ComponentVEvent8<A, B, C, D, E, F, G, H>) {
      invoke { arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7 ->
         componentVEvent.dispatch(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
      }
   }
}
