package com.asanme.youify.model

import com.asanme.youify.model.util.AppConstants.GOOGLE_AUTH_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getInstance(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(GOOGLE_AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}