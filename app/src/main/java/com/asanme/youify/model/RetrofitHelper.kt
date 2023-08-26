package com.asanme.youify.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val googleAuth = "https://www.googleapis.com"

    fun getInstance(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(googleAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}