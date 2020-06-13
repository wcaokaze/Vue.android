
Getter
================================================================================

```kotlin
val textState = state("Hello, World!")

textView.vBind.text { textState() }
textView.vBind.isVisible { textState().isNotEmpty() }
```
[ReactiveFieldとReactivatee](ReactiveFields-and-Reactivatees.md)の例で見たように、
`state` をViewにバインドするのがVue.androidの最も基本的な使い方です。

しかしこの方法だけで戦っていくのは無謀です。
すぐに限界が来ることは簡単に想像できるでしょう。

次の例を見てください。
```kotlin
val count = state(0)

koshian(this) {
   LinearLayout {
      view.orientation = HORIZONTAL

      TextView {
         vBind.text { count().toString() }
      }

      Button {
         view.text = "+"
         vOn.click { count.value++ }
      }

      Button {
         view.text = "-"
         vOn.click { count.value-- }
      }
   }
}
```
[トップページ](https://github.com/wcaokaze/Vue.android/blob/master/README.md)の
サンプルにあったカウンターを実装しようとしているところです。

0のときだけ特別に `"-"` と表示することにしましょう。
```kotlin
TextView {
   vBind.text {
      if (count() == 0) { "-" } else { count().toString() }
   }
}
```
これでも期待通りの動作をするのですが、Viewレイアウトを記述する部分に
なんだか複雑なロジックが混ざってしまいます。  
この程度で済めばいいですが、もっと複雑になってくるとどうでしょう？
シンプルでも宣言的でもなくなってしまいます。
さらに、同じロジックが複数箇所で必要な場合どうでしょう？
毎回コードをコピーするのでしょうか？

もちろんそんなことはしたくありませんから、こういうときは
`getter` という仕組みを使います。
```kotlin
val count = state(0)

val countText = getter {
   if (count() == 0) { "-" } else { count().toString() }
}

koshian(context) {
   ...
      TextView {
         vBind.text { countText() }
      }
   ...
}
```
ロジックは `countText` に移動し、バインドの記述はシンプルになりました。

```kotlin
count.value = 42
```
`count` に新しい値 `42` がセットされると `countText` は再計算され、
さらに `countText` にバインドされているTextViewの表示は自動的に `"42"` に更新されます。


Getter in Getter
--------------------------------------------------------------------------------

`getter` 内で別の `getter` を使用することができます。

```kotlin
val commit = state<Commit?>(null)
val author = getter { commit()?.author }
val authorName = getter { author()?.name }
```

少し難しい言い方をすれば、 `getter` はReactiveFieldであり、
同時にReactivateeでもあるということです。


例外
--------------------------------------------------------------------------------

通常は起こらないことですから、ほとんどの場合気にしないで大丈夫ですが、
getter内で例外がスローされるケースがあります。

例えばこう書いたとしましょう。
```kotlin
val urlString = state("https://example.com")
val url = getter { URL(urlString) }
```
[URLのコンストラクタ](https://docs.oracle.com/javase/jp/8/docs/api/java/net/URL.html#URL-java.lang.String-)
はURLのパースに失敗した場合に例外をスローしますから、
このgetterは計算中に例外をスローしてしまうかもしれません。

`try { URL(urlString) } catch (e: MalformedURLException) { null }` のように
書いておくべきだという話は今は置いておいて、仮に例外がスローされたとします。
```kotlin
urlString.value = "This is not a valid URL"
```
この場合、やはり `url` は再計算されるのですが、この時点では例外はスローされません。
`url` を使おうとした際にスローされるのです。
```kotlin
fun fetchSomething() {
   val url = try {
      url()
   } catch (e: MalformedURLException) {
      return
   }

   ...
}
```
この状態のことを私は「ポイズン状態 (poisoned)」と呼ぶことにしていますが、
まあ普通のVue.androidの使い方ではあまり使わない言葉でしょう。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [コードスタイル](CodeStyleRecommendation.md)  |  [VBindとVOnとVModel](VBind-and-VOn-and-VModel.md) →

