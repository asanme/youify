package com.asanme.youify.model.classes

data class YouTubeResponse(
    val nextPageToken: String?,
    val items: List<VideoSnippet>,
    val pageInfo: PageInfo
)

data class VideoSnippet(
    val snippet: VideoProperties
)

data class PageInfo(
    val totalResults: Int,
    val resultsPerPage: Int
)
