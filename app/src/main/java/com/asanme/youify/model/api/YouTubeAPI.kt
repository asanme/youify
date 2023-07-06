package com.asanme.youify.model.api

import com.asanme.youify.model.classes.YoutubeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Interface to make calls using Retrofit
interface YouTubeAPI {
    // TODO Add Header with the saved accessToken on SharedPreferences (or pass as parameter)
    @GET("/youtube/v3/videos")
    suspend fun getPlaylists(
        @Query("id") videoId: String,
        @Query("part") part: String,
        @Query("fields") fields: String,
        @Header("Authorization") accessToken: String
    ): Response<YoutubeResponse>

    @GET("/oauth2/v4/token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String,
        @Query("token_uri") part: String,
        @Query("fields") fields: String,
        @Header("Authorization") accessToken: String
    ): Response<YoutubeResponse>
}