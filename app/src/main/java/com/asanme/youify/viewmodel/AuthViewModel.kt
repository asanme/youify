package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.asanme.youify.model.api.YouTubeAPI
import com.asanme.youify.model.classes.PlaylistRequest
import com.asanme.youify.model.classes.YouTubeResponse
import com.asanme.youify.model.misc.AppConstants.CLIENT_ID
import com.asanme.youify.model.misc.HTTPResponseCodes.UNAUTHORIZED_REQUEST
import com.asanme.youify.model.routes.Routes
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class AuthViewModel(
    private val sharedPreferences: SharedPreferences,
    private val navController: NavHostController,
    private val api: YouTubeAPI
) : ViewModel() {
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

    private fun navigateTo(route: Routes) {
        navController.navigate(route.route)
    }

    // TODO Handle case for outdated accessToken on the same function
    suspend fun getVideoInfo(
        playlistRequest: PlaylistRequest
    ) = viewModelScope.launch {
        try {
            val accessToken = sharedPreferences.getString("accessToken", null)
            if (accessToken != null) {
                val header = "Bearer $accessToken"
                val response =
                    api.getPlaylists(
                        playlistRequest.playlistId,
                        playlistRequest.part,
                        playlistRequest.fields,
                        playlistRequest.maxResults,
                        playlistRequest.videoCategoryId,
                        header
                    )

                if (response.isSuccessful) {
                    handleResponseSuccess(response)
                } else {
                    handleResponseError(response, playlistRequest)
                }
            } else {
                // Edge Case?
            }
        } catch (err: Exception) {
            Log.e("RetrofitException", err.stackTraceToString())
        }
    }

    // TODO Handle Response
    private fun handleResponseSuccess(
        response: Response<YouTubeResponse>,
    ) {
        response.body().let { responseSuccess ->
            responseSuccess?.let {
                for (item in it.items) {
                    Log.i("YouTubeResponse", item.snippet.title)
                }
            }
        }
    }

    private suspend fun handleResponseError(
        response: Response<YouTubeResponse>,
        playlistRequest: PlaylistRequest
    ) {
        response.errorBody().let { responseError ->
            responseError?.let {
                try {
                    val error = JSONObject(it.string())
                    val statusCode = error.getJSONObject("error").getInt("code")
                    when (statusCode) {
                        UNAUTHORIZED_REQUEST -> {
                            refreshAccessToken(playlistRequest)
                        }
                    }

                    Log.e("YouTubeError", statusCode.toString())
                } catch (error: Exception) {
                    Log.e("DeserializationError", error.stackTraceToString())
                }
            }
        }
    }

    private suspend fun refreshAccessToken(
        playlistRequest: PlaylistRequest
    ) {
        try {
            val refreshToken = sharedPreferences.getString("refreshToken", "")

            if (refreshToken != null) {
                val response = api.refreshToken(
                    CLIENT_ID,
                    refreshToken,
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseSuccess ->
                        val newAccessToken = responseSuccess.accessToken
                        // NOTE: Used for testing on Postman
                        // Log.i("UpdatingAccessToken", newAccessToken)

                        updateEncryptedSharedPreferences(
                            refreshToken = refreshToken,
                            accessToken = newAccessToken
                        )

                        Log.i("RefreshAccessToken", "The token was successfully updated")

                        // After an unauthorized request, we create a new one
                        getVideoInfo(playlistRequest)
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

    /*
    suspend fun handleAuthResponse(
        tokenResponse: TokenResponse?
    ) = viewModelScope.launch {
        // TODO
    }

    fun goBack() {
        navController.navigateUp()
    }
    */
}