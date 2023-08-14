package com.asanme.youify.model.classes

data class YouTubeResponse(
    val nextPageToken: String?,
    val items: List<VideoSnippet>,
    val pageInfo: PageInfo
)