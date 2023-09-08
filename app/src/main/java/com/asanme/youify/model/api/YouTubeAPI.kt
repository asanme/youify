package com.asanme.youify.model.api

import com.asanme.youify.model.classes.AuthTokenResponse
import com.asanme.youify.model.classes.YouTubeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// Interface to make calls using Retrofit
interface YouTubeAPI {
    @GET("/youtube/v3/playlistItems")
    suspend fun getPlaylists(
        @Query("playlistId") playlistId: String,
        @Query("part") part: String,
        @Query("fields") fields: String,
        @Query("maxResults") maxResults: Int,
        @Query("videoCategoryId") videoCategoryId: Int,
        @Query("pageToken") pageToken: String?,
        @Header("Authorization") accessToken: String
    ): Response<YouTubeResponse>

    @POST("/oauth2/v4/token")
    suspend fun refreshToken(
        @Query("client_id") clientId: String,
        @Query("refresh_token") refreshToken: String,
        @Query("grant_type") grantType: String = "refresh_token"
    ): Response<AuthTokenResponse>
}