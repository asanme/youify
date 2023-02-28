package com.asanme.youify.model.auth

import androidx.core.net.toUri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationServiceDiscovery
import net.openid.appauth.ResponseTypeValues
import org.json.JSONObject


class AuthController(
    private val auth_config: String
) {
    private val MY_REDIRECT_URI =
        "com.asanme.youify:/oauth2redirect"

    private val MY_CLIENT_ID: String =
        "629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com"

    fun getAuthorizationRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            AuthorizationServiceDiscovery(
                JSONObject(auth_config)
            )
        )

        return AuthorizationRequest.Builder(
            serviceConfig,
            MY_CLIENT_ID,
            ResponseTypeValues.CODE,
            MY_REDIRECT_URI.toUri()
        ).setScope("https://www.googleapis.com/auth/youtube").build()
    }
}