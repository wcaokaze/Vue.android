
モジュール
================================================================================

アプリケーションの状態はひとつのVuexStoreが一元管理するべきなのですが、
アプリケーションが大きくなればなるほどVuexStoreも大きくなっていって、
そのうち管理できなくなってしまうということは言うまでもありません。

そういったときのためにVuexStoreは複数のモジュールに分割できるようになっています。

Vue.androidのモジュールシステムの使い方はMapに似ていて、
`modules[key]` の記法でサブモジュールを取得することができます。
```kotlin
state.modules[SUBMODULE_A].stateA
state.modules[SUBMODULE_B].stateB
action.modules[SUBMODULE_A].actionA()
```
Mapと違うのは、これらが完全に型付けされているという点です。

`state.modules[SUBMODULE_A]` は `SubmoduleAState` を返し、  
`state.modules[SUBMODULE_B]` は `SubmoduleBState` を返し、  
`action.modules[SUBMODULE_A]` は `SubmoduleAAction` を返す  
といったことが可能です。

私達は型を考えて慎重にキャストを書く必要もありませんし、
もちろん、実行時に型が一致せずにクラッシュしてしまうなんていう心配も無用です。


モジュールの作成
--------------------------------------------------------------------------------

これまでの例ではアプリケーションの状態として
「カートの中身」だけを扱ってきました。
さらに、「ログイン中のユーザー」と「アクセストークン」を
追加することを考えましょう。

```kotlin
class ApplicationState : VuexState() {
   val productsInCart = state<List<Product>>(emptyList())
   val currentUser = state<User?>(null)
   val accessToken = state<String?>(null)
}
```
Stateが大きくなって管理しきれなくなってしまいましたね。モジュールに分割しましょう。

ここではカートに関係するモジュールとログイン中のユーザーに関係するモジュールの
ふたつに分けてみます。  
モジュールに分けるといっても、単純にVuexStateをふたつ用意するだけです。
```kotlin
class ApplicationState : VuexState()

class CartState : VuexState() {
   val products = state<List<Product>>(emptyList())
}

class UserState : VuexState() {
   val currentUser = state<User?>(null)
   val accessToken = state<String?>(null)
}
```
同じようにMutation, Action, Getter, Storeも用意していきます。
```kotlin
class ApplicationStore
   : VuexStore<ApplicationState,
               ApplicationMutation,
               ApplicationAction,
               ApplicationGetter>()
{
   override fun createState()    = ApplicationState()
   override fun createMutation() = ApplicationMutation()
   override fun createAction()   = ApplicationAction()
   override fun createGetter()   = ApplicationGetter()
}

class CartStore
   : VuexStore<CartState,
               CartMutation,
               CartAction,
               CartGetter>()
{
   override fun createState()    = CartState()
   override fun createMutation() = CartMutation()
   override fun createAction()   = CartAction()
   override fun createGetter()   = CartGetter()
}

class UserStore
   : VuexStore<UserState,
               UserMutation,
               UserAction,
               UserGetter>()
{
   override fun createState()    = UserState()
   override fun createMutation() = UserMutation()
   override fun createAction()   = UserAction()
   override fun createGetter()   = UserGetter()
}
```

もう一息です。

最終的に `applicationState.modules[CART].products` という形になっていくわけですから、
キーにするための `CART` を用意して、ApplicationStoreのサブモジュールとして
CartStoreとUserStoreを宣言します。
```kotlin
class ApplicationStore
   : VuexStore<ApplicationState,
               ApplicationMutation,
               ApplicationAction,
               ApplicationGetter>()
{
   object ModuleKeys {
      val CART = Module.Key(CartStore::class)
      val USER = Module.Key(UserStore::class)
   }

   override fun createState()    = ApplicationState()
   override fun createMutation() = ApplicationMutation()
   override fun createAction()   = ApplicationAction()
   override fun createGetter()   = ApplicationGetter()

   override fun createModules() = listOf(
      Module(ModuleKeys.CART, CartStore()),
      Module(ModuleKeys.USER, UserStore())
   )
}
```
お疲れ様でした。


サブモジュールの取得
--------------------------------------------------------------------------------

`state.modules[ApplicationStore.ModuleKeys.CART]` の返り値の型は `CartState`、  
`getter.modules[ApplicationStore.ModuleKeys.CART]` の返り値の型は `CartGetter`、  
`getter.modules[ApplicationStore.ModuleKeys.USER]` の返り値の型は `UserGetter`  
という具合になっていきます。キャストは不要です。

```kotlin
state.modules[ApplicationStore.ModuleKeys.USER].currentUser
getter.modules[ApplicationStore.ModuleKeys.CART].productCount
action.modules[ApplicationStore.ModuleKeys.CART].fetchProducts()
```

`modules` は省略することができます。
```kotlin
state[ApplicationStore.ModuleKeys.USER].currentUser
getter[ApplicationStore.ModuleKeys.CART].productCount
action[ApplicationStore.ModuleKeys.CART].fetchProducts()
```

さらに短くしたいという人は、
ファイルの先頭で `import ApplicationStore.ModuleKeys.USER` と記述しましょう。
```kotlin
state[USER].currentUser
getter[CART].productCount
action[CART].fetchProducts()
```


モジュールのネスト
--------------------------------------------------------------------------------

サブモジュールにさらにサブモジュールを持たせてもいいです。
```kotlin
state.modules[Store.ModuleKeys.CHILD].modules[Child.ModuleKeys.GRANDCHILD]
```


VComponentへのサブモジュールの注入
--------------------------------------------------------------------------------

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
カートの状態がCartStoreに移動したことでCartButtonComponentに必要なVuexStoreは
ApplicationStoreではなくCartStoreになりました。

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
[サンプルアプリ](https://github.com/wcaokaze/Vue.android/tree/master/example)
をご覧ください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VComponentでVuexを使う](Use-Vuex-in-VComponent.md)

