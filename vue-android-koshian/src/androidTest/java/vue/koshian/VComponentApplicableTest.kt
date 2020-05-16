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

import androidx.test.ext.junit.rules.*
import androidx.test.ext.junit.runners.*
import org.junit.*
import org.junit.runner.*
import kotlin.test.*
import kotlin.test.Test

import android.content.*
import android.view.*
import koshian.*
import vue.*
import vue.vuex.*
import kotlin.contracts.*

class Store : VuexStore<State, Mutation, Action, Getter>() {
   override fun createState()    = State()
   override fun createMutation() = Mutation()
   override fun createAction()   = Action()
   override fun createGetter()   = Getter()
}

class State : VuexState()
class Mutation : VuexMutation<State>()
class Action : VuexAction<State, Mutation, Getter>()
class Getter : VuexGetter<State>()

class NoStoreComponent(context: Context) : VComponent<Nothing>() {
   companion object : KoshianNoStoreComponentConstructor<NoStoreComponent> {
      override fun instantiate(context: Context) = NoStoreComponent(context)
   }

   override val componentView = View(context)
   override val store: Nothing get() = throw UnsupportedOperationException()
}

class StoreComponent(context: Context, override val store: Store) : VComponent<Store>() {
   companion object : KoshianComponentConstructor<StoreComponent, Store> {
      override fun instantiate(context: Context, store: Store)
            = StoreComponent(context, store)
   }

   override val componentView = View(context)
}

@RunWith(AndroidJUnit4::class)
class VComponentApplicableTest {
   @get:Rule
   val activityScenarioRule = activityScenarioRule<EmptyTestActivity>()

   @Test fun addComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<NoStoreComponent>
         lateinit var componentInDsl: NoStoreComponent

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               componentApplicable = Component[NoStoreComponent] {
                  componentInDsl = component
               }
            }
         }

         assertSame(componentInDsl, componentApplicable.component)
         assertSame(rootView.getChildAt(0), componentApplicable.component.componentView)
      }
   }

   @Test fun applyComponent() {
      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<NoStoreComponent>

         @OptIn(ExperimentalContracts::class)
         val rootView = koshian(activity) {
            FrameLayout {
               componentApplicable = Component[NoStoreComponent] {
               }
            }
         }

         rootView.applyKoshian {
            componentApplicable {
               assertSame(componentApplicable.component, component)
            }
         }
      }
   }

   @Test fun manuallyInject() {
      val store = Store()

      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<StoreComponent>

         @OptIn(ExperimentalContracts::class)
         koshian(activity) {
            FrameLayout {
               componentApplicable = Component[StoreComponent, store] {
               }
            }
         }

         assertSame(store, componentApplicable.component.store)
      }
   }

   @Test fun inheritStore() {
      class InheritStoreComponent
            (context: Context, override val store: Store) : VComponent<Store>()
      {
         override val componentView: View
         val childComponent: StoreComponent

         init {
            @OptIn(ExperimentalContracts::class)
            koshian(context) {
               componentView = FrameLayout {
                  val childComponentApplicable = Component[StoreComponent] {
                  }

                  childComponent = childComponentApplicable.component
               }
            }
         }
      }

      val inheritStoreComponentConstructor = KoshianComponentConstructor { context, store: Store ->
         InheritStoreComponent(context, store)
      }

      val store = Store()

      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<InheritStoreComponent>

         @OptIn(ExperimentalContracts::class)
         koshian(activity) {
            FrameLayout {
               componentApplicable = Component[inheritStoreComponentConstructor, store] {
               }
            }
         }

         assertSame(store, componentApplicable.component.childComponent.store)
      }
   }

   @Test fun injectSubmodule() {
      val moduleKey = VuexStore.Module.Key<Store, State, Mutation, Action, Getter>()

      class ParentState : VuexState()
      class ParentMutation : VuexMutation<ParentState>()
      class ParentGetter : VuexGetter<ParentState>()
      class ParentAction : VuexAction<ParentState, ParentMutation, ParentGetter>()

      class ParentStore : VuexStore<ParentState, ParentMutation, ParentAction, ParentGetter> () {
         override fun createState()    = ParentState()
         override fun createMutation() = ParentMutation()
         override fun createAction()   = ParentAction()
         override fun createGetter()   = ParentGetter()

         override fun createModules() = listOf(
               Module(moduleKey, Store())
         )
      }

      class ParentComponent
            (context: Context, override val store: ParentStore) : VComponent<ParentStore>()
      {
         override val componentView: View
         val childComponent: StoreComponent

         init {
            @OptIn(ExperimentalContracts::class)
            koshian(context) {
               componentView = FrameLayout {
                  val childComponentApplicable = Component[StoreComponent, moduleKey] {
                  }

                  childComponent = childComponentApplicable.component
               }
            }
         }
      }

      val parentComponentConstructor = KoshianComponentConstructor { context, store: ParentStore ->
         ParentComponent(context, store)
      }

      val parentStore = ParentStore()
      val childStore = parentStore.modules[moduleKey]

      activityScenarioRule.scenario.onActivity { activity ->
         val componentApplicable: VComponentApplicable<ParentComponent>

         @OptIn(ExperimentalContracts::class)
         koshian(activity) {
            FrameLayout {
               componentApplicable = Component[parentComponentConstructor, parentStore] {
               }
            }
         }

         assertSame(childStore, componentApplicable.component.childComponent.store)
      }
   }

   @Test fun noStoreComponent_asASubComponent() {
      class InheritStoreComponent
            (context: Context, override val store: Store) : VComponent<Store>()
      {
         override val componentView: View

         init {
            @OptIn(ExperimentalContracts::class)
            koshian(context) {
               componentView = FrameLayout {
                  Component[NoStoreComponent] {
                  }
               }
            }
         }
      }

      val inheritStoreComponentConstructor = KoshianComponentConstructor { context, store: Store ->
         InheritStoreComponent(context, store)
      }

      val store = Store()

      activityScenarioRule.scenario.onActivity { activity ->
         @OptIn(ExperimentalContracts::class)
         koshian(activity) {
            FrameLayout {
               Component[inheritStoreComponentConstructor, store] {
               }
            }
         }
      }
   }
}
