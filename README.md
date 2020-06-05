
Vue.android
================================================================================

Immitates Vue.js on Android

Wait for me to create an example app.

```kotlin
val count = state(0)

val countText = getter {
   if (count() == 0) {
      "-"
   } else {
      count().toString()
   }
}

fun increment() {
   count.value++
}

fun decrement() {
   count.value = (count.value - 1).coerceAtLeast(0)
}

koshian(context) {
   LinearLayout {
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
```

