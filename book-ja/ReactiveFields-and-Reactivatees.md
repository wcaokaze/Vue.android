
ReactiveFieldとReactivatee
================================================================================

ReactiveFieldとReactivateeはVue.androidの最も便利で最も重要な概念です。

次のコードを見てください。
```kotlin
val textView = ... // findViewByIdなどでViewを取得
val textState = state("Hello, World!")

textView.vBind.text { textState() }
```
ここでTextViewには `"Hello, World!"` と表示されるのですが、重要なことは次です。
```kotlin
textState.value = "Hello, Vue!!"
```
textStateに新しい値をセットしました。このとき、TextViewの表示は自動的に
`"Hello, Vue!!"` に更新されるのです。

これがVue.androidのリアクティブシステムです。


この例で言うところの、
```kotlin
val textState = state("Hello, World!")
//              ^~~~~~~~~~~~~~~~~~~~~~
```
`state` のように、「新しい値を通知する機能」を持つインスタンスのことを
**ReactiveField** と呼び、

```kotlin
textView.vBind.text { textState() }
//                  ^~~~~~~~~~~~~~~
```
`{ textState() }` のように、「新しい値がセットされた際に再実行されるラムダ式」
のことを **Reactivatee** と呼びます。


さらに実用的な例を見てみましょう。

```kotlin
textView.vBind.isVisible { textState().isNotEmpty() }
```
今回は「textStateが空文字列ではない」という意味のReactivateeを
`isVisible` にバインドしています。
```kotlin
textState.value = ""
```
そしてtextStateに空文字列をセットするとTextViewは非表示になります。

わかりやすく、そして十分に短いコードですね。

あなたが今まで使っていたライブラリでは、どのようなコードになりますか？  
さあ、Vue.androidを始めましょう！


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

| [目次](../README-ja.md#チュートリアル) | [コードスタイル](CodeStyleRecommendation.md) →

