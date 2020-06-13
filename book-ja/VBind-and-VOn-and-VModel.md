
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
val inputText = state("")
editText.vModel.text(inputText)
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [Getter](Getters.md)

