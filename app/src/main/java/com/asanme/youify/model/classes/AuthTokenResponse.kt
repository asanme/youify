package com.asanme.youify.model.classes

data class AuthTokenResponse(
    val accessToken: String,
    val expiresIn: Int,
    val scope: String,
    val tokenType: String
)