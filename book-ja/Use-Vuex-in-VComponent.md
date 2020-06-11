
VComponentでVuexを使う
================================================================================

VComponent内でVuexを使ってみましょう。

```kotlin
class CartButtonComponent(context: Context) : VComponent<Nothing>() {
   override val store: Nothing get() = throw UnsupportedOperationException()
}
```
いままでは定型文のように `VComponent<Nothing>()` を継承していました。

ついにここにNothingではない型を書く時が来たのです。

1. まずは `Nothing` の代わりに `CartStore` を指定します
    ```kotlin
    class CartButtonComponent(context: Context) : VComponent<CartStore>() {
       override val store: CartStore get() = throw UnsupportedOperationException()
    }
    ```
1. コンストラクタにstoreを追加します
    ```kotlin
    class CartButtonComponent(context: Context, store: CartStore)
       : VComponent<CartStore>()
    {
       override val store: CartStore = store
    }
    ```
1. override宣言もコンストラクタに移動できます
    ```kotlin
    class CartButtonComponent(context: Context, override val store: CartStore)
       : VComponent<CartStore>()
    {
    }
    ```


Vuexへのアクセス
--------------------------------------------------------------------------------

VComponent内では `state`, `mutation`, `action`, `getter` というプロパティを
使うことができます。

```kotlin
class CartButtonComponent(context: Context, override val store: CartStore)
   : VComponent<CartStore>()
{
   override val componentView: View

   val onClick = vEvent0()

   init {
      koshian(context) {
         componentView = FrameLayout {
            vOn.click { onClick.emit() }

            ImageView {
               view.image = drawable(R.drawable.ic_shopping_cart)
            }

            TextView {
               view.background = drawable(R.drawable.red_circle)
               vBind.text { getter.productCount() }
               //           ^~~~~~
            }
         }
      }
   }
}
```


VComponentへのVuexの注入
--------------------------------------------------------------------------------

VComponentがコンストラクタでVuexStoreを受け取るようになったということは、
VComponentを使うときにはApplicationに持たせているVuexStoreを取ってきて
コンストラクタに渡さなければならなくなったということです。


### 手動注入 (Koshianなし)

Koshianなしではもう自力でインスタンス化して自力でaddViewするしかありませんから
すみませんがそうしてください
```kotlin
val store = (application as Application).cartStore
val cartButtonComponent = CartButtonComponent(context, store)

contentView.addView(cartButtonComponent.componentView,
      LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
```

### 明示的注入

`Component[::VComponent, store]` でVComponentにVuexStoreを注入して
インスタンス化できます。
このとき内部的には `constructor(Context, VuexStore)` を呼んでいますから、
このコンストラクタがないVComponentには注入できません。

```kotlin
class ShoppingActivity : Activity() {
   private val store: CartStore by lazy { (application as Application).cartStore }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      val contentView = koshian(this) {
         LinearLayout {
            view.orientation = VERTICAL

            // storeを明示してCartButtonComponentに注入します
            Component[::CartButtonComponent, store] {
            }
         }
      }

      setContentView(contentView)
   }
}
```

### 自動注入

VComponent内でVComponentを使う場合に、親Componentのstoreの型と
子Componentのstoreの型が一致していれば、自動的に親Componentのstoreが
子Componentに注入されます。

```kotlin
class ToolbarComponent(context: Context, override val store: CartStore)
   : VComponent<CartStore>()
{
   override val componentView: View

   val onMenuButtonClick = vEvent0()
   val onCartButtonClick = vEvent0()

   init {
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = HORIZONTAL

            ImageView {
               view.image = drawable(R.drawable.ic_menu)
               vOn.click { onMenuButtonClick.emit() }
            }

            TextView {
               view.text = "Shopping App Sample"
            }

            // ここでstoreを明示しなくても自動的にToolbarComponentのstoreが注入されます
            Component[::CartButtonComponent] {
               component.onClick { onCartButtonClick.emit() }
            }
         }
      }
   }
}
```


VComponentのテンプレート
--------------------------------------------------------------------------------

改めてVComponentのテンプレートをまとめます。

### Vuexを使わないVComponent
```kotlin
class FooComponent(context: Context) : VComponent<Nothing>() {
   override val store: Nothing get() = throw UnsupportedOperationException()

   override val componentView: View

   init {
      koshian(context) {
         componentView = FrameLayout {
         }
      }
   }
}
```

### Vuexを使うVComponent
```kotlin
class FooComponent(context: Context, override val store: FooStore)
   : VComponent<FooStore>()
{
   override val componentView: View

   init {
      koshian(context) {
         componentView = FrameLayout {
         }
      }
   }
}
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexStore](VuexStores.md)

