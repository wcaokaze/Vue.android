
コードスタイル
================================================================================

Vue.androidは大量のトップレベル関数から成り立っています。
コンパイラはそれらを完璧に解決できますが、我々には難しいです。
ですのでVue.androidの関数は `*` でimportすることをおすすめします。

```kotlin
import vue.*
```

### IntelliJ IDEA, Android Studioの設定

Settings > Editor > Code Style > Kotlin > Imports > Packages to Use Import with `*`

`vue` を追加して `With Subpackages` にチェックを入れましょう


Koshian
--------------------------------------------------------------------------------

[Koshian](https://github.com/wcaokaze/Koshian)はもともとVue.androidと一緒に
使うことを想定しています。Koshian以外のライブラリ(例えば、
Android標準のLayoutInflaterによるxmlや、
すでに非推奨ですが[Anko](https://github.com/Kotlin/anko)など)も使用できますが、
KoshianとVue.androidはきっと相性がいいはずです。

**LayoutInflater**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="8dp">

    <TextView
        android:id="@+id/counter_text"
        android:layout_width="0px"
        android:layout_height="wrap_content"
        android:layout_weight="1.0"
        android:layout_gravity="center_vertical"
        android:layout_marginHorizontal="16dp"
        android:gravity="end" />

    <Button
        android:id="@+id/btn_increment"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:text="+" />

    <Button
        android:id="@+id/btn_decrement"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:text="-" />

</LinearLayout>
```
```kotlin
class MainActivity : Activity() {
   private val count = state(0)

   private val countText = getter {
      if (count() == 0) { "-" } else { count().toString() }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      prepareVue()
   }

   private fun increment() {
      count.value++
   }

   private fun decrement() {
      count.value = (count.value - 1).coerceAtLeast(0)
   }

   private fun prepareVue() {
      setContentView(R.layout.activity_main)

      val counterTextView: TextView = findViewById(R.id.counter_text)
      counterTextView.vBind.text { countText() }

      val incrementButton: Button = findViewById(R.id.btn_increment)
      val decrementButton: Button = findViewById(R.id.btn_decrement)
      incrementButton.vOn.click { increment() }
      decrementButton.vOn.click { decrement() }
   }
}
```

**Koshian**
```kotlin
class MainActivity : Activity() {
   private val count = state(0)

   private val countText = getter {
      if (count() == 0) { "-" } else { count().toString() }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      prepareVue()
   }

   private fun increment() {
      count.value++
   }

   private fun decrement() {
      count.value = (count.value - 1).coerceAtLeast(0)
   }

   private fun prepareVue() {
      val contentView = koshian(this) {
         LinearLayout {
            view.orientation = HORIZONTAL

            TextView {
               vBind.text { countText() }
            }

            Button {
               view.text = "+"
               vOn.click { increment() }
            }

            Button {
               view.text = "-"
               vOn.click { decrement() }
            }
         }
      }

      contentView.applyKoshian {
         layout.width  = MATCH_PARENT
         layout.height = WRAP_CONTENT
         view.padding = 8.dp

         TextView {
            layout.width  = 0
            layout.height = WRAP_CONTENT
            layout.weight = 1.0f
            layout.gravity = CENTER_VERTICAL
            layout.horizontalMargin = 16.dp
            view.gravity = END
         }

         Button {
            layout.width  = WRAP_CONTENT
            layout.height = WRAP_CONTENT
            layout.gravity = CENTER_VERTICAL
         }

         Button {
            layout.width  = WRAP_CONTENT
            layout.height = WRAP_CONTENT
            layout.gravity = CENTER_VERTICAL
         }
      }

      setContentView(contentView)
   }
}
```
Koshianを使う場合は
[vue-android-koshian](https://github.com/wcaokaze/Vue.android/tree/master/vue-android-koshian)
を見てみてください。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [ReactiveFieldとReactivatee](ReactiveFields-and-Reactivatees.md)  |  [Getter](Getters.md) →

