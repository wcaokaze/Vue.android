
VuexStore
================================================================================

VuexState、VuexGetter、VuexMutation、VuexActionの4つをまとめるクラスがVuexStoreです。

このクラスはほとんど全てボイラープレートですから正直めんどうなのですが、
用意しましょう。
```kotlin
class CartStore : VuexStore<CartState, CartMutation, CartAction, CartGetter>() {
   override fun createState()    = CartState()
   override fun createMutation() = CartMutation()
   override fun createAction()   = CartAction()
   override fun createGetter()   = CartGetter()
}
```

お疲れ様です。これでVuexの準備ができました。


Application
--------------------------------------------------------------------------------

VuexStoreはApplicationに持たせておくことを推奨します。  
それ以外の場所、たとえばトップレベルのプロパティなどに格納しても使えるのですが、
特に理由がなければApplicationに置きましょう。
```kotlin
class Application : android.app.Application() {
   val cartStore by lazy { CartStore() }
}
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexAction](VuexActions.md)  |  [VComponentでVuexを使う](Use-Vuex-in-VComponent.md) →

