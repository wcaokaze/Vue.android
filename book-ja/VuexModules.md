
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


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VComponentでVuexを使う](Use-Vuex-in-VComponent.md)  |  [目次](../README-ja.md#チュートリアル)  | [VComponentへのサブモジュールの注入](Use-Vuex-Submodule-in-VComponent.md) →
