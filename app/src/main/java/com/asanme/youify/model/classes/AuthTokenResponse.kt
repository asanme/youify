package com.asanme.youify.model.classes

data class AuthTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val scope: String,
    val token_type: String
)