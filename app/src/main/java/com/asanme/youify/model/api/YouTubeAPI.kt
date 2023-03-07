package com.asanme.youify.model.api

import com.asanme.youify.model.classes.YoutubeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Interface to make calls using Retrofit
interface YouTubeAPI {
    // TODO Add Header with the saved accessToken on SharedPreferences (or pass as parameter)
    @GET("/youtube/v3")
    fun getPlaylists(
        @Query("id") videoId: String,
        @Query("part") part: String,
        @Query("fields") fields: String,
        // @Query("key") key: String, //Maybe not required (?)
    ): Response<YoutubeResponse>
}