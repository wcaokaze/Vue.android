
[日本語](README-ja.md)


Vue.android
================================================================================

Immitates Vue.js on Android

<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/counter_example.gif" width="405px">

```kotlin
val count = state(0)

val countText = getter {
   if (count() == 0) { "-" } else { count().toString() }
}

fun increment() {
   count.value++
}

fun decrement() {
   count.value = (count.value - 1).coerceAtLeast(0)
}

koshian(context) {
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
```


Code Style Recommendation
--------------------------------------------------------------------------------

Vue.android has too many top level functions. Compiler can resolve them surely,
but it's too hard for us. So it is recommended to import them with `*`.

```kotlin
import vue.*
```

### IntelliJ IDEA, Android Studio Settings

Settings > Editor > Code Style > Kotlin > Imports > Packages to Use Import with `*`

Add `vue` and check `With Subpackages`


Install
--------------------------------------------------------------------------------

Gradle
```groovy
dependencies {
   implementation 'com.wcaokaze.vue.android:vue-android-core:0.2.0'
}
```

Gradle (Kotlin)
```kotlin
dependencies {
   implementation("com.wcaokaze.vue.android:vue-android-core:0.2.0")
}
```

