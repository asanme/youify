package com.asanme.youify.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    // TODO change URL
    val googleAuth = ""

    fun getInstance(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(googleAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}