
VuexStateとVuexGetter
================================================================================

VuexState
--------------------------------------------------------------------------------

まず `VuexState` ですが、stateしか持っていないクラスです。

```kotlin
class CartState : VuexState() {
   val products = state<List<Product>>(emptyList())
}
```

これだけです。
getterを書いたり、stateを変更する処理を書いたりしてはいけません。

使い方は今まで使ってきたstateと全く同じですから、
追加で説明することは特にありません。


VuexGetter
--------------------------------------------------------------------------------

`VuexGetter` はgetterしか持っていないクラスです。

```kotlin
class CartGetter : VuexGetter<CartState>() {
   val productCount = getter { state.products().size }
}
```
こちらも今までのgetterと同じです。
stateにアクセスするために `state.products` と書いている点には注意してください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [Vuexとは？](What-is-Vuex.md)  |  [VuexMutation](VuexMutations.md) →

