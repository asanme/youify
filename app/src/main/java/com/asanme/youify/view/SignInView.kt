package com.asanme.youify.view

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.asanme.youify.R
import com.asanme.youify.model.auth.AuthController
import net.openid.appauth.AuthorizationResponse

@Composable
fun SignInView() {
    val context = LocalContext.current
    val authController = AuthController(context)
    val authService by remember { mutableStateOf(authController.getAuthService()) }
    val authIntent by remember { mutableStateOf(authController.getAuthIntent(authService)) }

    var text by remember { mutableStateOf("default") }

    // TODO Implement this inside a ViewModel
    val url = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { response ->
            response.data?.let { intent ->
                AuthorizationResponse.fromIntent(intent)?.let { authResponse ->
                    authService.performTokenRequest(
                        authResponse.createTokenExchangeRequest()
                    ) { response, authException ->
                        if (response != null) {
                            response.refreshToken?.let { refreshToken ->
                                response.accessToken?.let { accessToken ->
                                    Log.i("RefreshToken", refreshToken)
                                    Log.i("RefreshToken", accessToken)

                                    checkEncryptedSharedPreferences(
                                        context,
                                        refreshToken,
                                        accessToken
                                    )
                                }
                            }
                        } else {
                            authException?.let {
                                Log.e("RefreshTokenError", authException.stackTraceToString())
                            }
                        }
                    }
                }
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Welcome! $text",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )

        Text(
            "Sign In to continue",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )

        Button(
            onClick = {
                url.launch(authIntent)
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sign In using Google")

                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = stringResource(id = R.string.google_icon),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun checkEncryptedSharedPreferences(
    context: Context,
    refreshToken: String,
    accessToken: String
) {
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    val sharedPrefsFile = "tokenKeys"

    val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        sharedPrefsFile,
        mainKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    with(sharedPreferences.edit()) {
        putString("refreshToken", refreshToken)
        putString("accessToken", accessToken)
        apply()
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_6
)
@Composable
private fun SignInPreview() {
    SignInView()
}