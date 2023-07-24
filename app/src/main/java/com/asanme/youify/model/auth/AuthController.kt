package com.asanme.youify.model.auth

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.asanme.youify.R
import com.asanme.youify.model.misc.APIConstants.CLIENT_ID
import com.asanme.youify.model.misc.APIConstants.REDIRECT_URI
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationServiceDiscovery
import net.openid.appauth.ResponseTypeValues
import org.json.JSONObject


class AuthController(
    private val context: Context
) {
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
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI.toUri()
        ).setScope("https://www.googleapis.com/auth/youtube").build()
    }

    fun getAuthService(): AuthorizationService {
        return AuthorizationService(context)
    }

    fun getAuthIntent(authService: AuthorizationService): Intent {
        return authService.getAuthorizationRequestIntent(getAuthorizationRequest())
    }
}