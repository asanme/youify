package com.asanme.youify.model.auth

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import net.openid.appauth.*
import org.json.JSONObject


class AuthController(
    private val auth_config: String
) {
    private val redirectURI =
        "com.asanme.youify:/oauth2redirect"

    private val clientId: String =
        "629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com"

    private fun getAuthorizationRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            AuthorizationServiceDiscovery(
                JSONObject(auth_config)
            )
        )

        return AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectURI.toUri()
        ).setScope("https://www.googleapis.com/auth/youtube").build()
    }

    fun getAuthIntent(context: Context): Intent {
        val authRequest = AuthController(auth_config).getAuthorizationRequest()
        val authService = AuthorizationService(context)
        return authService.getAuthorizationRequestIntent(authRequest)
    }
}