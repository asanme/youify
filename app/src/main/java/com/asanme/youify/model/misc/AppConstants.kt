package com.asanme.youify.model.misc

object AppConstants {
    const val CLIENT_ID: String =
        "629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com"

    const val REDIRECT_URI = "com.asanme.youify:/oauth2redirect"

    const val RESULTS_PER_PAGE = 50

    const val VIDEO_PROPERTIES =
        "pageInfo,nextPageToken,items(snippet(title, thumbnails.high(url), thumbnails.maxres(url)))"
}