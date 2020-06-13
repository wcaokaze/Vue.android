
サンプル
================================================================================

このサンプルアプリはVue.androidを使ったマストドンクライアントです。

<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/vue-android-example.gif" width="405px">

下記のオープンソースライブラリを使用しています。

[Koin](https://github.com/InsertKoinIO/koin)  
[Koshian](https://github.com/wcaokaze/Koshian)  
[Ktor](https://github.com/ktorio/ktor)  
[Material design icons](https://github.com/google/material-design-icons)  


Android Studioで開く
--------------------------------------------------------------------------------

1. このリポジトリをcloneします  
   ```sh
   git clone https://github.com/wcaokaze/Vue.android
   ```

1. Android Studioで開きます  
    リポジトリのルート(Vue.android)ではなく、
    exampleアプリのディレクトリ(Vue.android/example)を開いてください


目次
--------------------------------------------------------------------------------

### 基本

認証画面
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_auth.png" width="405px">

[AuthActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/auth/AuthActivity.kt)  


### RecyclerView

タイムライン画面
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_timeline.png" width="405px">

[TimelineActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/timeline/TimelineActivity.kt)  
[TimelineRecyclerViewAdapter](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/timeline/TimelineRecyclerViewAdapter.kt)  


### VComponent

トゥート画面
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_status.png" width="405px">

[StatusActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/StatusActivity.kt)  
[StatusComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/StatusComponent.kt)  
[AccountComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/AccountComponent.kt)  
[FooterComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/FooterComponent.kt)  


### Vuex

[Store](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/Store.kt)  
[MastodonStore](https://github.com/wcaokaze/Vue.android/blob/master/example/mastodon/src/main/java/com/wcaokaze/vue/android/example/mastodon/MastodonStore.kt)  
[CredentialPreferenceStore](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/CredentialPreferenceStore.kt)  

