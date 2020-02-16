package vue.vuex

abstract class VuexAction<S, M, G>
      where S : VuexState,
            M : VuexMutation<S>,
            G : VuexGetter<S>
