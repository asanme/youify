package com.asanme.youify.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.asanme.youify.model.routes.Routes
import kotlinx.coroutines.launch
import net.openid.appauth.TokenResponse

class AuthViewModel(
    private val sharedPreferences: SharedPreferences,
    private val navController: NavHostController
) : ViewModel() {
    suspend fun handleAuthResponse(
        tokenResponse: TokenResponse?
    ) = viewModelScope.launch {
        // TODO
    }

    fun tokenExists(): Boolean {
        return sharedPreferences.contains("refreshToken")
    }

    fun updateEncryptedSharedPreferences(
        refreshToken: String,
        accessToken: String
    ) = viewModelScope.launch {
        with(sharedPreferences.edit()) {
            putString("refreshToken", refreshToken)
            putString("accessToken", accessToken)
            apply()
        }

        navigateTo(Routes.HomeViewRoute)
    }

    fun goBack() {
        navController.navigateUp()
    }

    fun navigateTo(route: Routes) {
        navController.navigate(route.route)
    }
}