package com.wcaokaze.vue.android.example.mastodon

import com.wcaokaze.vue.android.example.mastodon.infrastructure.*
import org.kodein.di.*

class Mastodon(override val kodein: Kodein) : KodeinAware {
   internal fun getMastodonInstance(rootUrl: String) = MastodonInstance(kodein, rootUrl)
}
