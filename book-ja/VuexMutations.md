
VuexMutation
================================================================================

VuexStateとVuexGetterにはstateを変更する処理を書いてはいけないと説明しました。

では、stateを変更する処理はどこに書くべきなのかというと、VuexMutationです。

```kotlin
class ApplicationState : VuexState() {
   val productsInCart = state<List<Product>>(emptyList())
}

class ApplicationMutation : VuexMutation<ApplicationState>() {
   fun addProductToCart(product: Product) {
      state.productsInCart.value += product
   }

   fun removeAllProductsFromCart() {
      state.productsInCart.value = emptyList()
   }
}
```


### VuexStateはVuexMutation以外から変更不能

> VuexStateとVuexGetterにはstateを変更する処理を書いてはいけないと説明しましたが、
> 実は書けないようにできています。  
> そんな馬鹿なと思った人は試してみてください。
> ```kotlin
> class ApplicationState : VuexState() {
>    val productsInCart = state<List<Product>>(emptyList())
>
>    fun addProductToCart(product: Product) {
>       productsInCart.value += product
>       //             ^~~~~
>       //             VuexState can be written only via VuexMutation
>    }
> }
> ```


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

← [VuexStateとVuexGetter](VuexStates-and-VuexGetters.md)  |  [VuexAction](VuexActions.md) →

