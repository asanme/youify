package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.asanme.youify.model.api.YouTubeAPI
import com.asanme.youify.model.routes.Routes
import kotlinx.coroutines.launch
import net.openid.appauth.TokenResponse
import org.json.JSONObject

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

    fun navigateTo(route: Routes) {
        navController.navigate(route.route)
    }

    suspend fun refreshAccessToken() {

    }

    suspend fun getVideoInfo(
        videoId: String,
        part: String,
        fields: String,
    ) = viewModelScope.launch {
        try {
            val accessToken = sharedPreferences
                .getString("accessToken", null)
                .orEmpty()

            val header = "Bearer $accessToken"

            val request = api.getPlaylists(videoId, part, fields, header)
            request.body().let { response ->
                response?.let {
                    Log.i("YouTubeResponse", it.items[0].snippet.title)
                }
            }

            request.errorBody().let { responseError ->
                responseError?.let {
                    try {
                        val error = JSONObject(it.string())
                        Log.e(
                            "YouTubeError",
                            error.getJSONObject("error").getString("status").lowercase()
                        )
                    } catch (error: Exception) {
                        Log.e("DeserializationError", error.stackTraceToString())
                    }
                }
            }
        } catch (err: Exception) {
            Log.e("RetrofitException", err.stackTraceToString())
        }
    }
}