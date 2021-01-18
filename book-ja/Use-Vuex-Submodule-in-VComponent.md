
VComponentへのサブモジュールの注入
================================================================================

話は少し戻ってVComponent内でVuexを使うときの話です。

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
            }
         }
      }
   }
}
```
カートの状態がApplicationStoreからCartStoreに移動したことで
CartButtonComponentに必要なVuexStoreはApplicationStoreではなくCartStoreになりました。

仮にここでApplicationStoreを受け取って `getter[CART].productCount()`
と記述しても結果は同じですが、やはりCartButtonComponentである以上
余計なものを受け取らずCartStoreを受け取るべきだというのは
技術的にも芸術的にも間違いない事実です。

一方でToolbarComponentが使うVuexStoreはApplicationStoreのままです。
ここでは実装していませんが、ログイン中のユーザー名を表示したり、
アイコンを表示したりという改修が起こり得ますし、
それはToolbarComponentの責務として全く正しいでしょう。
```kotlin
class ToolbarComponent(context: Context, override val store: ApplicationStore)
   : VComponent<ApplicationStore>()
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

            Component[::CartButtonComponent, store.modules[CART]] {
               component.onClick { onCartButtonClick.emit() }
            }
         }
      }
   }
}
```

ToolbarComponent内でCartButtonComponentを使うのですが、
[自動注入](Use-Vuex-in-VComponent.md#自動注入)ができません。
親Componentのstoreの型(ApplicationStore)と
子Componentのstoreの型(CartStore)が一致しなくなったからです。

上記の例ではこのように書いています。
```kotlin
Component[::CartButtonComponent, store.modules[CART]] {}
```
これは[明示的注入](Use-Vuex-in-VComponent.md#明示的注入)です。

もちろん明示的注入でもいいですが、もう少し省略する記法もあります。
```kotlin
Component[::CartButtonComponent, CART] {}
```
親Componentのstoreのサブモジュールを子Componentに注入する場合には、
サブモジュールのキーのみでOKです。


卒業式
--------------------------------------------------------------------------------

お疲れ様でした。  
Vue.androidの長いチュートリアルがようやく終わりました。

実際に動いているVue.androidのソースコードを見たいのであれば
[サンプルアプリ](https://github.com/wcaokaze/Vue.android/blob/master/example/README-ja.md)
をご覧ください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [モジュール](VuexModules.md)  |  [目次](../README-ja.md#チュートリアル)  |

