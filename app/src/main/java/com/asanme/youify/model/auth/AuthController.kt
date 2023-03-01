package com.asanme.youify.model.auth

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.asanme.youify.R
import net.openid.appauth.*
import org.json.JSONObject


class AuthController(
    private val context: Context
) {
    private val redirectURI =
        "com.asanme.youify:/oauth2redirect"

    private val clientId: String =
        "629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com"

    private fun readJsonFile(): String =
        context.resources.openRawResource(R.raw.auth_config).bufferedReader().use { it.readText() }

    private fun getAuthorizationRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            AuthorizationServiceDiscovery(
                JSONObject(readJsonFile())
            )
        )

        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectURI.toUri()
        ).setScope("https://www.googleapis.com/auth/youtube").build()
    }

    fun getAuthService(): AuthorizationService {
        return AuthorizationService(context)
    }

    fun getAuthIntent(authService: AuthorizationService): Intent {
        return authService.getAuthorizationRequestIntent(getAuthorizationRequest())
    }
}