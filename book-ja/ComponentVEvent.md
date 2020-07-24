
ComponentVEvent
================================================================================

ViewからvOnでイベントを受け取るのと同じように、VComponentからイベントを
受け取りたいことがあります。

前回に引き続き、AccountComponentのアイコン部分がタップされたらアカウントの詳細を
表示する実装をしてみましょう。

<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_status.png" width="405px">

```kotlin
class TootComponent(context: Context) : VComponent<Nothing>() {
   override val componentView: View

   val toot = vBinder<Toot>()

   private fun showTooterDetail() {
      ...
   }

   init {
      koshian(context) {
         componentView = LinearLayout {
            view.orientation = VERTICAL

            Component[::AccountComponent] {
               component.account { toot()?.tooter }

               // vOn.iconClick { showTooterDetail() }
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
さて、 `AccountComponent` からどうやって「アイコンがタップされた」という
イベントを受け取ればいいのでしょうか？

コメントアウトしていますが
```kotlin
vOn.iconClick { showTooterDetail() }
```
と書ければ一番嬉しいですね。

結論から言えばこのようには書けないのですが、かなり近い形にすることができます。
こうなります。
```kotlin
component.onIconClick { showTooterDetail() }
```

vOnのときと同じように、アイコンがタップされるとラムダ式が実行され、
`showTooterDetail` が呼ばれます。

さて、イベントの受け取り方はわかりました。ではイベントを送信する側、つまり
`AccountComponent` はどのように実装すればよいのでしょう？

`ComponentVEvent` を使います。

```kotlin
class AccountComponent(context: Context) : VComponent<Nothing>() {
   override val componentView: View

   val account = vBinder<Account>()

   val onIconClick = vEvent0()
   //                ^~~~~~~~~

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
               vOn.click { onIconClick.emit() }
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
`vEvent0` を用意し、 `emit()` を呼び出すことでイベントを発火することができます。

`vEvent0` から `vEvent8` まで用意されていますから、引数とともに
イベントを発火することもできます。

```kotlin
private val count = state(0)

val onIncrement = vEvent1<Int>()

private fun increment() {
   count.value++
   onIncrement.emit(count.value)
}

init {
   koshian(context) {
      LinearLayout {
         view.orientation = HORIZONTAL

         TextView {
            vBind.text { count.toString() }
         }

         Button {
            view.text = "+"
            vOn.click { increment() }
         }
      }
   }
}
```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [ComponentVBind](ComponentVBinder.md)  |  [目次](../README-ja.md#チュートリアル)  |  →

