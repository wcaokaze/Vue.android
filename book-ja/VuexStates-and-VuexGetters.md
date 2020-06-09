
VuexStateとVuexGetter
================================================================================

VuexState
--------------------------------------------------------------------------------

まず `VuexState` ですが、 `state` しか持っていないクラスです。

```kotlin
class CartState : VuexState() {
   val products = state<List<Product>>(emptyList())
}
```

これだけです。
`getter` を書いたり、 `state` を変更する処理を書いたりしてはいけません。

使い方は今まで使ってきた `state` と全く同じですから、
特に追加で説明することはありません。


VuexGetter
--------------------------------------------------------------------------------

`VuexGetter` は `getter` しか持っていないクラスです。

```kotlin
class CartGetter : VuexGetter<CartState>() {
   val productCount = getter { state.products().size }
}
```
こちらも今までの `getter` と同じです。
`state` にアクセスするために `state.products` と書いている点に注意してください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [Vuexとは？](What-is-Vuex.md)

