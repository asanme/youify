package com.asanme.youify

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.asanme.youify.model.RetrofitHelper
import com.asanme.youify.model.api.YouTubeAPI
import com.asanme.youify.model.routes.Routes
import com.asanme.youify.ui.theme.YouifyTheme
import com.asanme.youify.view.HomeView
import com.asanme.youify.view.SignInView
import com.asanme.youify.viewmodel.AuthViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouifyTheme {
                Surface {
                    App()
                }
            }
        }
    }
}

@Composable
private fun App() {
    val api = RetrofitHelper.getInstance().create(YouTubeAPI::class.java)
    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel = AuthViewModel(
        getSharedPreferences(context),
        navController,
        api
    )

    NavHost(
        navController = navController,
        startDestination = if (!authViewModel.tokenExists()) {
            Routes.SignInViewRoute.route
        } else {
            Routes.HomeViewRoute.route
        }
    ) {
        composable(Routes.SignInViewRoute.route) {
            SignInView(authViewModel)
        }

        composable(Routes.HomeViewRoute.route) {
            HomeView(authViewModel)
        }
    }
}

fun getSharedPreferences(context: Context): SharedPreferences {
    val sharedPreferencesFilename = "secret"
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    return EncryptedSharedPreferences.create(
        sharedPreferencesFilename,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}