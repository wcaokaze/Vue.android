
VComponent
================================================================================

VComponentは、他のVComponentから使いやすいように小さくしたVueインスタンスです。
Vue.androidを使った開発では、小さなVComponentを組み合わせて
徐々に大きいVComponentを作っていくようにするとうまくいきやすいです。

カウンターを部品化して、いろんなところで使えるようにしてみましょう。
```kotlin
class CounterComponent(context: Context) : VComponent<Nothing>() {
   override val store: Nothing get() = throw UnsupportedOperationException()

   override val componentView: View

   private val count = state(0)

   private val countText = getter {
      if (count() == 0) { "-" } else { count().toString() }
   }

   private fun increment() {
      count.value++
   }

   private fun decrement() {
      count.value = (count.value - 1).coerceAtLeast(0)
   }

   init {
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = HORIZONTAL

            TextView {
               vBind.text { countText() }
            }

            Button {
               view.text = "+"
               vOn.click { increment() }
            }

            Button {
               view.text = "-"
               vOn.click { decrement() }
            }
         }
      }
   }
}
```
`VComponent<Nothing>()` を継承して、 `store` と `componentView` を
オーバーライドします。

`<Nothing>` と `override val store: Nothing` については今は気にしないでください。
後述する「Vuex」のStoreを使用する場合に使うプロパティですが、
今回はVuexを使わないのでNothingを指定して例外をスローしています。


VComponentを使う
--------------------------------------------------------------------------------

カウンターを他のViewに追加してみましょう。

Koshianを使っていない場合は手動でaddViewしてください。
```kotlin
val counterComponent = CounterComponent(context)

val componentContainer: LinearLayout = findViewById(R.id.component_container)

componentContainer.addView(0, counterComponent.componentView,
      LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
```

Koshianの場合は次のように書きます。
```kotlin
val counterComponent: CounterComponent

koshian(context) {
   LinearLayout {
      view.orientation = VERTICAL

      counterComponent = Component[::CounterComponent] {
         layout.width  = MATCH_PARENT
         layout.height = WRAP_CONTENT
      }
   }
}
```

カウンターを追加できました。


VComponentInterface
--------------------------------------------------------------------------------

VComponentを継承したいけど、すでに他のクラスを継承していてVComponentを
継承できない場合があります。

VComponentInterfaceを使ってください。
```kotlin
class CounterComponent(context: Context) : VComponentInterface<Nothing> {
   override val componentLifecycle = ComponentLifecycle(this)
   override val store: Nothing get() = throw UnsupportedOperationException()

   override val componentView: View
   ...
}
```
`componentLifecycle` を実装する手間だけが増えますが、他はVComponentと全く同じです。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

[ComponentLifecycle](ComponentLifecycle.md) →

