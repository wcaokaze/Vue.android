
VuexAction
================================================================================

ここまでの話を簡単にまとめるとこうなります。

- VuexState  
    stateのみを持つ。つまり状態のみを持つ
- VuexGetter  
    getterのみを持つ。つまり状態の頻繁に使うプロパティや、
    複数の状態を組み合わせて計算した結果などを持つ
- VuexMutation  
    stateを変更する処理のみを持つ

VuexActionの役割はこれ以外の処理になるのですが、多くの場合、

『新しい状態を作って、Mutationを呼び、Stateを新しい状態に更新する』

ということになります。

Web APIに接続してデータをダウンロードすることもあるでしょうし、
データベースやファイルを読み込むこともあるでしょう。
あるいは、なんらかの引数を受け取って計算を行い、
その結果をStateに反映するといった処理もVuexActionが担当することになります。

```kotlin
class CartAction : VuexAction<CartState, CartMutation, CartGetter>() {
   suspend fun fetchProducts() {
      val products = try {
         WebApiService.fetchProducts()
      } catch (e: IOException) {
         throw CancellationException()
      }

      mutation.setProducts(products)
   }
}
```


他の要素へのアクセス
--------------------------------------------------------------------------------

VuexAction内では、VuexMutation、VuexState、VuexGetterのすべてにアクセスできます。
現在の状態を元にして新しい状態を決めるような処理も可能ということです。  

ただし、Stateを直接変更することはここでも禁止されますから、
状態の更新には必ずVuexMutationを経由する必要があります。
```kotlin
class CartAction : VuexAction<CartState, CartMutation, CartGetter>() {
   suspend fun saveProducts() {
      try {
         WebApiService.postProducts(state.products())
      } catch (e: IOException) {
         throw CancellationException()
      }
   }

   suspend fun fetchProducts() {
      val products = try {
         WebApiService.fetchProducts()
      } catch (e: IOException) {
         throw CancellationException()
      }

      state.products.value = products
      //             ^~~~~
      //             VuexState can be written only via VuexMutation
   }
}
```


CoroutineDispatcher
--------------------------------------------------------------------------------

多くの場合、Actionはsuspend funになります(もちろん必須ではありませんが)。

Dispatcherの管理に少し気をつけてください。

VuexはDispatchers.Mainで実行されることを前提としています。  
Web APIやファイルIOなどを一時的にバックグラウンドスレッドで行うことは問題ありませんが、
*Mutationの呼び出しはメインスレッドで行うように注意してください。*

```kotlin
suspend fun foo() {
   // ここはメインスレッド

   withContext(Dispatchers.Default) {
      // ここはバックグラウンド
      // ここでMutationを呼び出してはいけない
   }

   // ここはメインスレッド
}
```
コルーチンをよく理解している人は気づいたでしょうが、これはつまり、
Action自体が(この例では `foo` が)、Dispatchers.Mainで呼び出されないといけない
ということになります。

Vue.androidで起動するコルーチンはDispatchers.Mainで実行するようにできていますから
通常はあまり気にしなくても大丈夫ですが、通常ではない使い方をする場合は
kotlinx.coroutinesの使い方をよく理解することをおすすめします。
```kotlin
class FooComponent : VComponent<Nothing>() {
   fun callWithLaunch() {
      launch {
         // VComponent内で起動したコルーチンはDispatchers.Mainで実行されています。
         action.foo()
      }
   }

   init {
      // vOnで実行するコルーチンもDispatchers.Mainで実行されています。
      button.vOn.click { foo() }
   }
}
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexMutation](VuexMutations.md)  |  [VuexStore](VuexStores.md) →

