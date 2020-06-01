/*
 * Copyright 2020 wcaokaze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wcaokaze.vue.android.example.activity.auth

import android.annotation.*
import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.widget.*
import com.wcaokaze.vue.android.example.*
import com.wcaokaze.vue.android.example.BuildConfig
import com.wcaokaze.vue.android.example.Store.ModuleKeys.CREDENTIAL_PREFERENCE
import com.wcaokaze.vue.android.example.activity.timeline.*
import com.wcaokaze.vue.android.example.mastodon.auth.*
import koshian.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.*
import vue.*
import vue.koshian.*
import java.net.*
import kotlin.contracts.*

class AuthActivity : Activity(), VComponentInterface<Store> {
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: FrameLayout

   override val store: Store by inject()

   private val authorizator by lazy { MastodonAuthorizator() }

   private val instanceUrl = state<CharSequence>("https://")
   private val errorMessage = state<String?>(null)

   private val client = state<Client?>(null)

   private val clientRegistrationJob = state(Job.completed())
   private val isRegisteringClient = getter { clientRegistrationJob().toReactiveField()() }

   private val credentialPublishingJob = state(Job.completed())
   private val isPublishingCredential = getter { credentialPublishingJob().toReactiveField()() }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()

      watcher(getter[CREDENTIAL_PREFERENCE].credential, immediate = true) {
         if (it != null) {
            startTimelineActivity()
         }
      }
   }

   override fun onNewIntent(intent: Intent?) {
      super.onNewIntent(intent)

      val data = intent?.data ?: return
      if (!data.toString().contains(BuildConfig.REDIRECT_URI)) { return }
      val authCode = data.getQueryParameter("code") ?: return
      publishCredential(authCode)
   }

   private fun startTimelineActivity() {
      startActivity(
         Intent(this, TimelineActivity::class.java))

      finish()
   }

   private fun startAuthorization() {
      clientRegistrationJob.value = launch {
         errorMessage.value = null

         val instanceUrl = try {
            val instanceUrlStr = instanceUrl.value.toString().takeIf { it.isNotBlank() }
            URL(instanceUrlStr)
         } catch (e: Exception) {
            errorMessage.value = "Invalid URL"
            throw CancellationException()
         }

         val authorizationUrl = try {
            val registeredClient = authorizator
               .registerClient(instanceUrl, BuildConfig.REDIRECT_URI)

            client.value = registeredClient

            authorizator.getAuthorizationUrl(registeredClient)
         } catch (e: Exception) {
            errorMessage.value = "Something goes wrong"
            throw CancellationException()
         }

         val intent = Intent(Intent.ACTION_VIEW,
               Uri.parse(authorizationUrl.toExternalForm()))

         startActivity(intent)
      }
   }

   private fun publishCredential(authCode: String) {
      credentialPublishingJob.value = launch {
         val client = client()

         if (client == null) {
            errorMessage.value = "Something goes wrong"
            throw CancellationException()
         }

         val credential = try {
            authorizator.publishCredential(client, authCode)
         } catch (e: Exception) {
            errorMessage.value = "Something goes wrong. Please try again later."
            throw CancellationException()
         }

         mutation[CREDENTIAL_PREFERENCE].setCredential(credential)
      }
   }

   @SuppressLint("SetTextI18n")
   @OptIn(ExperimentalContracts::class)
   private fun buildContentView() {
      val instanceUrlView: EditText
      val errorMessageView: TextView
      val progressBar: ProgressBar
      val startButton: Button

      val tokenReceiverView: TextView

      koshian(this) {
         componentView = FrameLayout {
            LinearLayout {
               view.orientation = VERTICAL
               vBind.isVisible { !isPublishingCredential() }

               instanceUrlView = EditText {
                  vModel.text(instanceUrl)
               }

               errorMessageView = TextView {
                  vBind.text(errorMessage)
               }

               LinearLayout {
                  view.orientation = HORIZONTAL

                  progressBar = ProgressBar {
                     vBind.isVisible(isRegisteringClient)
                  }

                  startButton = Button {
                     view.text = "GO"
                     vOn.click { startAuthorization() }
                  }
               }
            }

            tokenReceiverView = TextView {
               view.text = "wait..."
               vBind.isVisible(isPublishingCredential)
            }
         }
      }

      componentView.applyKoshian {
         view.padding = 16.dp

         LinearLayout {
            layout.width  = MATCH_PARENT
            layout.height = MATCH_PARENT

            instanceUrlView {
               layout.width = MATCH_PARENT
            }

            errorMessageView {
               layout.gravity = END
               view.textColor = 0xff0000.opaque
               view.typeface = BOLD
            }

            LinearLayout {
               layout.width = MATCH_PARENT

               Space {
                  layout.width  = 0
                  layout.height = 0
                  layout.weight = 1.0f
               }

               progressBar {
                  layout.gravity = CENTER_VERTICAL
                  layout.width  = 24.dp
                  layout.height = 24.dp
               }

               startButton {
                  layout.margins = 8.dp
                  layout.gravity = CENTER_VERTICAL
               }
            }
         }

         tokenReceiverView {
            layout.horizontalMargin = 8.dp
            layout.verticalMargin   = 4.dp
         }
      }

      setContentView(componentView)
   }
}
