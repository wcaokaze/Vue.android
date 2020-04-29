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

package com.wcaokaze.vue.android.example.auth

import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.widget.*
import com.wcaokaze.vue.android.example.mastodon.auth.*
import koshian.*
import kotlinx.coroutines.*
import vue.*
import vue.koshian.*
import java.net.*
import kotlin.contracts.*

class AuthActivity : Activity(), VComponentInterface {
   override val componentLifecycle = ComponentLifecycle(this)

   override lateinit var componentView: LinearLayout

   private val instanceUrl = state<CharSequence>("https://")
   private val errorMessage = state<String?>(null)
   private val authorizationJob = state<Job>(Job().also { it.complete() })

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      buildContentView()
   }

   private fun startAuthorization() {
      authorizationJob.value = launch {
         errorMessage.value = null

         val instanceUrl = try {
            val instanceUrlStr = instanceUrl.value.toString().takeIf { it.isNotBlank() }
            URL(instanceUrlStr)
         } catch (e: Exception) {
            errorMessage.value = "Invalid URL"
            throw CancellationException()
         }

         try {
            val client = registerClient(instanceUrl)
            val authorizationUrl = getAuthorizationUrl(client)

            val intent = Intent(Intent.ACTION_VIEW,
                  Uri.parse(authorizationUrl.toExternalForm()))

            startActivity(intent)
         } catch (e: Exception) {
            errorMessage.value = "Something goes wrong"
         }
      }
   }

   private fun buildContentView() {
      @OptIn(ExperimentalContracts::class)
      componentView = koshian(this) {
         LinearLayout {
            view.orientation = VERTICAL

            EditText {
               vModel.text(instanceUrl)
            }

            TextView {
               vBind.text(errorMessage)
            }

            LinearLayout {
               view.orientation = HORIZONTAL

               ProgressBar {
                  vBind.isVisible { authorizationJob().toReactiveField()() }
               }

               Button {
                  view.text = "GO"
                  vOn.click { startAuthorization() }
               }
            }
         }
      }

      componentView.applyKoshian {
         view.padding = 16.dip

         EditText {
            layout.width = MATCH_PARENT
         }

         TextView {
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

            ProgressBar {
               layout.gravity = CENTER_VERTICAL
               layout.width  = 24.dip
               layout.height = 24.dip
            }

            Button {
               layout.margins = 8.dip
               layout.gravity = CENTER_VERTICAL
            }
         }
      }

      setContentView(componentView)
   }
}
