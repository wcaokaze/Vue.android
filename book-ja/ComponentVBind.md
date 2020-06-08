
ComponentVBind
================================================================================

ViewにvBindで値を渡すのと同じように、VComponentに値を渡したいことがあります。

例えば
[サンプルアプリ](https://github.com/wcaokaze/Vue.android/tree/master/example)内の
トゥートを表示しているところですが、次のようになっていきます。
(実際はもう少し複雑ですが、説明のために簡単にしています)

<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_status.png" width="405px">

```kotlin
class TootComponent(context: Context) : VComponent<Nothing>() {
   override val componentView: View

   val toot = state<Toot?>(null)

   init {
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = VERTICAL

            Component[::AccountComponent] {
               // vBind.account { toot()?.tooter }
            }

            TextView {
               vBind.text { toot()?.content }
            }

            TextView {
               vBind.text { toot()?.tootedDate }
            }
         }
      }
   }
}
```
さて、困ったことになりました。
`AccountComponent` にどうやって `tooter` を渡せばいいのでしょうか？

コメントアウトしていますが
```kotlin
vBind.account { toot()?.tooter }
```
と書ければ一番嬉しいですね。

結論から言えばこのようには書けないのですが、かなり近い形にすることができます。
こうなります。
```kotlin
component.account { toot()?.tooter }
```

vBindのときと同じように、tootに新しい値がセットされると、 `{ toot()?.tooter }`
が再実行され、AccountComponentに新しい値が渡されます。

さて、値の渡し方はわかりました。では値を受け取る側、つまり `AccountComponent` は
どのように実装すればよいのでしょう？

`ComponentVBind` を使います。

```kotlin
class AccountComponent(context: Context) : VComponent<Nothing>() {
   override val componentView: View

   val account = vBinder<Account>()
   //            ^~~~~~~~~~~~~~~~~~

   private val acct = getter {
      val acct = account()?.acct
      if (acct == null) { null } else { "@$acct" }
   }

   init {
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = HORIZONTAL

            ImageView {
               vBind.imageBitmap { account()?.icon }
            }

            LinearLayout {
               view.orientation = VERTICAL

               TextView {
                  vBind.text { account()?.name }
               }

               TextView {
                  vBind.text { acct() }
               }
            }
         }
      }
   }
}
```
今までは `state` を使っていたところに代わりに `vBinder` を使いました。

これでvBindのように振る舞い、
`account { toot()?.tooter }` というバインドの記述ができるようになります。

その代わり、 `state` では使えていた手動で新しい値をセットする方法、つまり
`account.value = tooter` は使えなくなっています。

少しややこしいですから、今はよくわからないという人は、基本的な
`state`の使い方に慣れてから、あるいはもしあなたが今疲れているのであれば、
一旦寝て頭を休めてから明日もう一度読んでみてください。

#### state
```kotlin
val account = state<Account?>(null)

account.value = tooter // OK
account { reactiveField().tooter } // Error

textView.vBind.text { account()?.name } // OK
```

#### vBinder
```kotlin
val account = vBinder<Account>()

account.value = tooter // Error
account { reactiveField().tooter } // OK

textView.vBind.text { account()?.name } // OK
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [ComponentLifecycle](ComponentLifecycle.md)

