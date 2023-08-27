package com.asanme.youify.model.classes

import com.google.gson.annotations.SerializedName

// We can use the annotation @SerializedName() to access the Json values correctly
data class AuthTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("scope") val scope: String,
    @SerializedName("token_type") val tokenType: String
)