
Vue.android
================================================================================

Vue.jsをAndroid上で模倣するライブラリです

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


チュートリアル
--------------------------------------------------------------------------------

#### 基本

[ReactiveFieldとReactivatee](book-ja/ReactiveFields-and-Reactivatees.md)  
[コードスタイル](book-ja/CodeStyleRecommendation.md)  
[Getter](book-ja/Getters.md)  
[VBindとVOnとVModel](book-ja/VBind-and-VOn-and-VModel.md)  

#### Component

[VComponent](book-ja/VComponents.md)  
[ComponentLifecycle](book-ja/ComponentLifecycle.md)  
[ComponentVBind](book-ja/ComponentVBind.md)  
[ComponentVEvent](book-ja/ComponentVEvent.md)  

#### Vuex

[Vuexとは？](book-ja/What-is-Vuex.md)  
[VuexStateとVuexGetter](book-ja/VuexStates-and-VuexGetters.md)  
[VuexMutation](book-ja/VuexMutations.md)  
[VuexAction](book-ja/VuexActions.md)  
[VuexStore](book-ja/VuexStores.md)  
[VComponentでVuexを使う](book-ja/Use-Vuex-in-VComponent.md)  


コードスタイル
--------------------------------------------------------------------------------

Vue.androidは大量のトップレベル関数から成り立っています。
コンパイラはそれらを完璧に解決できますが、我々には難しいです。
ですのでVue.androidの関数は `*` でimportすることをおすすめします。

```kotlin
import vue.*
```

### IntelliJ IDEA, Android Studioの設定

Settings > Editor > Code Style > Kotlin > Imports > Packages to Use Import with `*`

`vue` を追加して `With Subpackages` にチェックを入れましょう


インストール
--------------------------------------------------------------------------------

Gradle
```groovy
dependencies {
   implementation 'com.wcaokaze.vue.android:vue-android-core:0.1.0'
}
```

Gradle (Kotlin)
```kotlin
dependencies {
   implementation("com.wcaokaze.vue.android:vue-android-core:0.1.0")
}
```

