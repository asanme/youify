package com.asanme.youify

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
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
        // Disable default fitting to system windows to enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            YouifyTheme {
                // Root surface uses systemBarsPadding to avoid overlaps
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
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

    // Scaffold provides insets-aware paddingValues
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (!authViewModel.tokenExists()) {
                Routes.SignInViewRoute.route
            } else {
                Routes.HomeViewRoute.route
            },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            composable(Routes.SignInViewRoute.route) {
                SignInView(authViewModel)
            }
            composable(Routes.HomeViewRoute.route) {
                HomeView(authViewModel)
            }
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
