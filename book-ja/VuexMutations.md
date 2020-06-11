
VuexMutation
================================================================================

VuexStateとVuexGetterにはstateを変更する処理を書いてはいけないと説明しました。

では、stateを変更する処理はどこに書くべきなのかというと、VuexMutationです。

```kotlin
class CartState : VuexState() {
   val products = state<List<Product>>(emptyList())
}

class CartMutation : VuexMutation<CartState>() {
   fun addProduct(product: Product) {
      state.products.value += product
   }

   fun removeAllProducts() {
      state.products.value = emptyList()
   }
}
```


### VuexStateはVuexMutation以外から変更不能

> VuexStateとVuexGetterにはstateを変更する処理を書いてはいけないと説明しましたが、
> 実は書けないようにできています。  
> そんな馬鹿なと思った人は試してみてください。
> ```kotlin
> class CartState : VuexState() {
>    val products = state<List<Product>>(emptyList())
>
>    fun addProduct(product: Product) {
>       products.value += product
>       //       ^~~~~
>       //       VuexState can be written only via VuexMutation
>    }
> }
> ```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexStateとVuexGetter](VuexStates-and-VuexGetters.md)  |  [VuexAction](VuexActions.md) →

