package com.asanme.youify

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.asanme.youify.model.auth.AuthController
import com.asanme.youify.ui.theme.YouifyTheme
import net.openid.appauth.AuthorizationService


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouifyTheme {
                Surface {
                    App()
                }
            }
        }
    }
}

@Composable
private fun App() {
    val context = LocalContext.current
    val authRequest = AuthController(
        context.resources.openRawResource(R.raw.auth_config).bufferedReader().use { it.readText() }
    ).getAuthorizationRequest()

    val authService = AuthorizationService(context)
    val authIntent: Intent = authService.getAuthorizationRequestIntent(authRequest)

    // TODO Redirect upon recieving server response to authenticate
    val pendingIntent = PendingIntent.getActivity(
        context,
        1,
        authIntent,
        PendingIntent.FLAG_IMMUTABLE
    ).send() // This calls the Intent
}