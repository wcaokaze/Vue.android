
VuexStore
================================================================================

VuexState、VuexGetter、VuexMutation、VuexActionの4つをまとめるクラスがVuexStoreです。

このクラスはほとんど全てボイラープレートですから正直めんどうなのですが、
用意しましょう。
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
```

お疲れ様です。これでVuexの準備ができました。


Application
--------------------------------------------------------------------------------

VuexStoreはApplicationに持たせておくことを推奨します。  
それ以外の場所、たとえばトップレベルのプロパティなどに格納しても使えるのですが、
特に理由がなければApplicationに置きましょう。
```kotlin
class Application : android.app.Application() {
   val store by lazy { ApplicationStore() }
}
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexAction](VuexActions.md)  |  [目次](../README-ja.md#チュートリアル)  |  [VComponentでVuexを使う](Use-Vuex-in-VComponent.md) →

