package com.asanme.youify.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    // TODO change URL
    val googleAuth =
        "https://accounts.google.com/o/oauth2/v2/auth?client_id=629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com&redirect_uri=com.asanme.youify&response_type=code&scope=https://www.googleapis.com/auth/youtube"

    val testWeb = "https://accounts.google.com/o/oauth2/v2/auth?client_id=629936952678-re74p0n7qt56581mvg68mkgjes3cqhp2.apps.googleusercontent.com&redirect_uri=http://127.0.0.1:4000/&response_type=code&scope=https://www.googleapis.com/auth/youtube& client_secret=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"

    fun getInstance(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(googleAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}