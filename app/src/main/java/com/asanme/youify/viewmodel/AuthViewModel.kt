package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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


// TODO Remove all suspend functions and instead, create the coroutine inside the ViewModel
// Docs: https://developer.android.com/kotlin/coroutines/coroutines-best-practices#viewmodel-coroutines
class AuthViewModel(
    private val sharedPreferences: SharedPreferences,
    private val navController: NavHostController,
    private val api: YouTubeAPI
) : ViewModel() {
    private val videoList = mutableStateListOf<VideoSnippet>()
    private val _videoListFlow = MutableStateFlow(videoList)

    // TODO REMOVE (?)
    val videoListFlow = _videoListFlow.asStateFlow()

    fun tokenExists(): Boolean = sharedPreferences.contains("refreshToken")

    fun clearVideos() = videoList.clear()

    fun removeVideo(video: VideoSnippet) = videoList.remove(video)

    private fun navigateTo(route: Routes) = navController.navigate(route.route)

    // TODO Remove automatic navigation
    // TODO Check if removing courotine causes freezes
    fun updateEncryptedSharedPreferences(
        refreshToken: String,
        accessToken: String
    ) {
        with(sharedPreferences.edit()) {
            putString("refreshToken", refreshToken)
            putString("accessToken", accessToken)
            apply()
        }

        navigateTo(Routes.HomeViewRoute)
    }


    // TODO Handle case for outdated accessToken on the same function
    // TODO Check if the provided playlist is a mix / radio / stream since the videos are infinite
    fun getVideoInfo(
        playlistRequest: PlaylistRequest
    ) = viewModelScope.launch {
        try {
            val accessToken = sharedPreferences.getString("accessToken", null) ?: return@launch
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
                handleResponseException(response, playlistRequest)
            }
        } catch (err: Exception) {
            Log.e("RetrofitException", err.stackTraceToString())
        }
    }


    // TODO Fix access
    private fun handleResponseSuccess(
        response: Response<YouTubeResponse>,
        playlistRequest: PlaylistRequest
    ) {
        response.body().let { responseSuccess ->
            responseSuccess?.let { youtubeResponse ->
                videoList.addAll(youtubeResponse.items)
                val hasMoreVideos = (youtubeResponse.nextPageToken != null)

                // Debug only
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

    // TODO Fix access
    private fun handleResponseException(
        response: Response<YouTubeResponse>,
        playlistRequest: PlaylistRequest
    ) {
        response.errorBody().let { responseException ->
            responseException?.let {
                try {
                    val error = JSONObject(it.string())
                    val statusCode = error.getJSONObject("error").getInt("code")
                    when (statusCode) {
                        UNAUTHORIZED_REQUEST -> {
                            refreshAccessToken(playlistRequest)
                        }
                    }

                    Log.e("YouTubeException", statusCode.toString())
                } catch (error: Exception) {
                    Log.e("DeserializationException", error.stackTraceToString())
                }
            }
        }
    }

    // TODO Fix access
    private fun refreshAccessToken(
        playlistRequest: PlaylistRequest
    ) = viewModelScope.launch {
        try {
            // If the refresh token is null it cannot be accessed
            val refreshToken = sharedPreferences.getString("refreshToken", "") ?: return@launch
            val response = api.refreshToken(CLIENT_ID, refreshToken)

            if (response.isSuccessful) {
                response.body()?.let { responseSuccess ->
                    val newAccessToken = responseSuccess.accessToken
                    // NOTE: Used for testing on Postman
                    // Log.i("UpdatingAccessToken", newAccessToken)

                    updateEncryptedSharedPreferences(refreshToken, newAccessToken)

                    // After an unauthorized request we make another request
                    Log.i("RefreshAccessToken", "The token was updated successfully")
                    getVideoInfo(playlistRequest)
                }
            } else {
                response.errorBody().let { responseException ->
                    responseException?.let {
                        try {
                            val error = JSONObject(it.string())
                            val statusCode = error.getJSONObject("error").getInt("code")
                            Log.e("YouTubeException", statusCode.toString())
                        } catch (error: Exception) {
                            Log.e("DeserializationException", error.stackTraceToString())
                        }
                    }
                }
            }
        } catch (err: Exception) {
            Log.e("RefreshAccessTokenException", err.stackTraceToString())
        }
    }
}