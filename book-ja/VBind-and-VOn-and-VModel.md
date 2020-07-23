
VBindとVOnとVModel
================================================================================

すでに何度か例に出てきていますね。

VBind
--------------------------------------------------------------------------------

VBindはReactiveFieldをバインドすることができます。
```kotlin
textView.vBind.text { string() }
textView.vBind.textColor { if (isValid()) { 0x000000.opaque } else { 0xff0000.opaque } }
```


VOn
--------------------------------------------------------------------------------

VOnはViewのイベントを受け取ることができます。
```kotlin
button.vOn.click { saveData() }
```
記法こそVBindに似ていますが、こちらはイベントが発生した際に
ラムダ式を実行するだけの全くシンプルなものです。

このラムダ式はsuspend関数ですので他のsuspend funを呼び出す際に
いちいちコルーチンをlaunchする必要はありません。


VModel
--------------------------------------------------------------------------------

VModelは双方向バインディングです。ReactiveFieldの更新をViewに反映し、
ユーザーの操作によるViewの更新をReactiveFieldに反映します。
```kotlin
val editTextContent = state<CharSequence>("")
editText.vModel.text(editTextContent)
```
editTextに新しい文字が入力されるとそれがeditTextContentにも反映され、
editTextContentに新しい値がセットされるとそれがeditTextにも反映されるという具合です。

ここで `<CharSequence>` の記述が必須であることに気をつけてください。
これには少し複雑な理由があります。

とりあえずVue.androidを使いたいだけなんだという人は、
ここで `<CharSequence>` の記述が必要だということだけを覚えておいて
下記の説明は読み飛ばしても大丈夫ですが、理解したいという人は是非読んでみてください。

VModelは言わば下記コードの糖衣構文のようなものです。
```kotlin
val editTextContent = state<CharSequence>("")
editTextContent.addObserver { editText.setText(it) }
editText.doAfterTextChanged { editTextContent.value = it }
```

もちろん実際にはもっと複雑ですが、極端に単純化すれば、2つの動作から成り立っています。

1. editTextContentの新しい値をeditTextにセットする
1. editTextの新しい値をeditTextContentにセットする

ここで問題になるのは、EditTextから受け取る値の型とEditTextに渡せる値の型が
実は一致していないということです。
```kotlin
fun setText(text: CharSequence?)
fun doAfterTextChanged(action: (Editable) -> Unit)
```

継承関係は次のようになっています。
```
  CharSequence        CharSequence
  Spanned             String
  Spannable
  Editable
```

普通にいままで通りの感覚で `state("")` と書いてしまうと
型は `state<String>("")` になってしまいます。

先程挙げたVModelの動作をひとつずつ見てみましょう。

1. editTextContentの新しい値をeditTextにセットする  
    ```kotlin
    val editTextContent = state<String>("")
    editTextContent.addObserver { newValue: String -> editText.setText(newValue) }
    ```
    `EditText.setText` は `CharSequence?` を受け取るので
    そのサブタイプである `String` を渡すことは可能です。  
    これは問題ありません。
1. editTextの新しい値をeditTextContentにセットする  
    ```kotlin
    val editTextContent = state<String>("")
    editText.doAfterTextChanged { text: Editable -> editTextContent.value = text }
    ```
    `Editable` は `String` のサブタイプではありませんので
    editTextContentへ格納することはできません。  
    ここがダメなわけです。

ではどうすればよいのか、もうお分かりですね？  
`EditText.setText` に渡すことができて、 `Editable` を格納可能な型を使えばよいのです。

すべて挙げると、下記の8通りになります。
```kotlin
val editTextContent = state<CharSequence>(...)
val editTextContent = state<CharSequence?>(...)
val editTextContent = state<Spanned>(...)
val editTextContent = state<Spanned?>(...)
val editTextContent = state<Spannable>(...)
val editTextContent = state<Spannable?>(...)
val editTextContent = state<Editable>(...)
val editTextContent = state<Editable?>(...)
```

そしてダメな例は下記です。
```kotlin
state<String>(...) // setTextに渡せるがEditableを格納できない
state<Any>(...) // Editableを格納できるけどsetTextに渡せない
state<Int>(...) // なにもかもダメ
```

もちろん間違った型を指定するとコンパイラがエラーを出しますので安心してください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [Getter](Getters.md)

