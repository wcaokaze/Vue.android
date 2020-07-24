
ComponentLifecycle
================================================================================

VComponentはActivityに表示されている間、アクティブです。

```kotlin
class ComponentImpl : VComponent<Nothing>() {
   init {
      componentLifecycle.onAttachedToActivity += {
         ...
      }
   }
}
```
ComponentLifecycleを使うことでアクティブ/非アクティブの切り替えを
監視することができるのですが、この方法はわかりやすくありませんから、
普通はあまり使いません。


CoroutineScope
--------------------------------------------------------------------------------

VComponentはCoroutineScopeを実装しています。

これはつまり、VComponent内で起動したコルーチンは、そのVComponentが
非アクティブになると自動的にキャンセルされるということです。
```kotlin
class ComponentImpl : VComponent<Nothing>() {
   fun fetchData() {
      launch {
         ...
      }
   }
}
```

また、VComponent内では `vOn` もそのCoroutineScopeを受け継ぎます。
つまり、vOnでなんらかの時間がかかるsuspend funを呼んでいる場合には、
VComponentが非アクティブになるとやはり自動的にキャンセルされるということです。
```kotlin
class ComponentImpl : VComponent<Nothing>() {
   suspend fun calculate() {
      ...
   }

   init {
      button.vOn.click { calculate() }
   }
}
```


watcher
--------------------------------------------------------------------------------

ReactiveFieldを監視することができます。
```kotlin
watcher(reactiveField) { value ->
   ...
}
```
今まではReactiveFieldはViewにバインドすることしかできませんでしたが、
監視ができるようになったことでより柔軟なトリガーなどを実装することが可能になります。

VComponentが非アクティブになったとき、watcherは自動的に監視をやめますから、
監視しぱなしになることでパフォーマンスに影響したり、メモリリークの原因になる
心配はありません。


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VComponent](VComponents.md)  |  [ComponentVBind](ComponentVBinder.md) →

