package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.asanme.youify.model.api.YouTubeAPI
import com.asanme.youify.model.classes.YouTubeResponse
import com.asanme.youify.model.misc.APIConstants.CLIENT_ID
import com.asanme.youify.model.routes.Routes
import kotlinx.coroutines.launch
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import retrofit2.Response

class AuthViewModel(
    private val sharedPreferences: SharedPreferences,
    private val navController: NavHostController,
    private val api: YouTubeAPI
) : ViewModel() {
    suspend fun handleAuthResponse(
        tokenResponse: TokenResponse?
    ) = viewModelScope.launch {
        // TODO
    }

    fun tokenExists(): Boolean {
        return sharedPreferences.contains("refreshToken")
    }

    fun updateEncryptedSharedPreferences(
        refreshToken: String,
        accessToken: String
    ) = viewModelScope.launch {
        with(sharedPreferences.edit()) {
            putString("refreshToken", refreshToken)
            putString("accessToken", accessToken)
            apply()
        }

        navigateTo(Routes.HomeViewRoute)
    }

    fun goBack() {
        navController.navigateUp()
    }

    private fun navigateTo(route: Routes) {
        navController.navigate(route.route)
    }

    // TODO Handle case for outdated accessToken on the same function
    suspend fun getVideoInfo(
        videoId: String,
        part: String,
        fields: String,
    ) = viewModelScope.launch {
        try {
            val accessToken = sharedPreferences.getString("accessToken", null)
            if (accessToken != null) {
                val header = "Bearer $accessToken"
                val response = api.getPlaylists(videoId, part, fields, header)

                if (response.isSuccessful) {
                    handleResponseSuccess(response)
                } else {
                    handleResponseError(response)
                }
            } else {
                // Edge Case?
            }
        } catch (err: Exception) {
            Log.e("RetrofitException", err.stackTraceToString())
        }
    }

    // TODO Handle Response
    private fun handleResponseSuccess(response: Response<YouTubeResponse>) {
        response.body().let { responseSuccess ->
            responseSuccess?.let {
                Log.i("YouTubeResponse", it.items[0].snippet.title)
            }
        }
    }

    private suspend fun handleResponseError(response: Response<YouTubeResponse>) {
        response.errorBody().let { responseError ->
            responseError?.let {
                try {
                    val error = JSONObject(it.string())
                    val statusCode = error.getJSONObject("error").getInt("code")
                    when (statusCode) {
                        401 -> {
                            refreshAccessToken()
                        }
                    }

                    Log.e("YouTubeError", statusCode.toString())
                } catch (error: Exception) {
                    Log.e("DeserializationError", error.stackTraceToString())
                }
            }
        }
    }

    private suspend fun refreshAccessToken() {
        try {
            val refreshToken = sharedPreferences.getString("refreshToken", "")

            if (refreshToken != null) {
                val response = api.refreshToken(
                    CLIENT_ID,
                    refreshToken,
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseSuccess ->
                        val newAccessToken = responseSuccess.access_token
                        Log.i("UpdatingAccessToken", newAccessToken)

                        updateEncryptedSharedPreferences(
                            refreshToken = refreshToken,
                            accessToken = newAccessToken
                        )
                    }
                } else {
                    response.errorBody().let { responseError ->
                        responseError?.let {
                            try {
                                val error = JSONObject(it.string())
                                val statusCode = error.getJSONObject("error").getInt("code")
                                Log.e("YouTubeError", statusCode.toString())
                            } catch (error: Exception) {
                                Log.e("DeserializationError", error.stackTraceToString())
                            }
                        }
                    }
                }
            }
        } catch (err: Exception) {
            Log.e("RefreshAccessTokenException", err.stackTraceToString())
        }
    }
}