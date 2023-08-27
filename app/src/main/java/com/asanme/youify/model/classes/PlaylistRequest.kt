package com.asanme.youify.model.classes

data class PlaylistRequest(
    val playlistId: String,
    val part: String,
    val fields: String,
    val maxResults: Int,
    val videoCategoryId: Int
)