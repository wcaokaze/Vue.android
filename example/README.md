
Example
================================================================================

This sample app is a mastodon client that uses Vue.android.

<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/vue-android-example.gif" width="405px">

This app uses the follow OSS libraries.

[Koin](https://github.com/InsertKoinIO/koin)  
[Koshian](https://github.com/wcaokaze/Koshian)  
[Ktor](https://github.com/ktorio/ktor)  
[Material design icons](https://github.com/google/material-design-icons)  


Open in Android Studio
--------------------------------------------------------------------------------

1. Clone this repo  
    ```sh
    git clone https://github.com/wcaokaze/Vue.android
    ```

1. Open in Android Studio  
    Open the directory of this example app (Vue.android/example),
    not the repo root (Vue.android).


Index
--------------------------------------------------------------------------------

### Basic

Auth screen<br />
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_auth.png" width="405px">

[AuthActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/auth/AuthActivity.kt)  


### RecyclerView

Timeline screen<br />
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_timeline.png" width="405px">

[TimelineActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/timeline/TimelineActivity.kt)  
[TimelineRecyclerViewAdapter](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/timeline/TimelineRecyclerViewAdapter.kt)  


### VComponent

Toot screen<br />
<img src="https://raw.github.com/wcaokaze/Vue.android/master/imgs/example_status.png" width="405px">

[StatusActivity](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/StatusActivity.kt)  
[StatusComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/StatusComponent.kt)  
[AccountComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/AccountComponent.kt)  
[FooterComponent](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/activity/status/FooterComponent.kt)  


### Vuex

[Store](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/Store.kt)  
[MastodonStore](https://github.com/wcaokaze/Vue.android/blob/master/example/mastodon/src/main/java/com/wcaokaze/vue/android/example/mastodon/MastodonStore.kt)  
[CredentialPreferenceStore](https://github.com/wcaokaze/Vue.android/blob/master/example/app/src/main/java/com/wcaokaze/vue/android/example/CredentialPreferenceStore.kt)  


