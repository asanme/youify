package com.asanme.youify.model.util

object AppConstants {
    const val GOOGLE_AUTH_URL = "https://www.googleapis.com"

    const val YOUTUBE_API_URL = "https://www.googleapis.com/auth/youtube"

    const val CLIENT_ID: String =
        "629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com"

    const val REDIRECT_URI = "com.asanme.youify:/oauth2redirect"

    const val RESULTS_PER_PAGE = 50

    const val VIDEO_PROPERTIES =
        "pageInfo,nextPageToken,items(snippet(title, thumbnails.high(url), thumbnails.maxres(url)))"
}