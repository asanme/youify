package com.asanme.youify.model.classes

import com.google.gson.annotations.SerializedName

data class VideoProperties(
    val title: String,
    @SerializedName("thumbnails")
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    val high: High,
    @SerializedName("maxres")
    val maxRes: MaxRes,
)

data class High(
    val url: String
)

data class MaxRes(
    val url: String
)
