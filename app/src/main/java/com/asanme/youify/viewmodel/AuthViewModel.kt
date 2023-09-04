package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.asanme.youify.model.api.YouTubeAPI
import com.asanme.youify.model.classes.PlaylistRequest
import com.asanme.youify.model.classes.VideoSnippet
import com.asanme.youify.model.classes.YouTubeResponse
import com.asanme.youify.model.misc.AppConstants.CLIENT_ID
import com.asanme.youify.model.misc.HTTPResponseCodes.UNAUTHORIZED_REQUEST
import com.asanme.youify.model.routes.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class AuthViewModel(
    private val sharedPreferences: SharedPreferences,
    private val navController: NavHostController,
    private val api: YouTubeAPI
) : ViewModel() {
    private val _userVideos = MutableStateFlow<List<VideoSnippet>>(emptyList())
    var userVideos = _userVideos.asStateFlow()

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

    suspend fun clearVideos() {
        _userVideos.emit(emptyList())
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
                        playlistRequest.pageToken,
                        header
                    )

                if (response.isSuccessful) {
                    handleResponseSuccess(response, playlistRequest)
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

    private suspend fun handleResponseSuccess(
        response: Response<YouTubeResponse>,
        playlistRequest: PlaylistRequest
    ) {
        response.body().let { responseSuccess ->
            responseSuccess?.let { youtubeResponse ->
                val newList = arrayListOf<VideoSnippet>()
                newList.addAll(_userVideos.value)
                newList.addAll(youtubeResponse.items)

                _userVideos.emit(newList)
                val hasMoreVideos = (youtubeResponse.nextPageToken != null)

                for (item in youtubeResponse.items) {
                    Log.i("YouTubeResponse", item.snippet.title)
                }

                if (hasMoreVideos) {
                    val newRequest = playlistRequest.copy(pageToken = youtubeResponse.nextPageToken)
                    getVideoInfo(newRequest)
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