package com.asanme.youify.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.navigation.NavHostController
import com.asanme.youify.R
import com.asanme.youify.getSharedPreferences
import com.asanme.youify.model.auth.AuthController
import com.asanme.youify.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

@Composable
fun SignInView(
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val authController = AuthController(context)
    val authService by remember { mutableStateOf(authController.getAuthService()) }
    val authIntent by remember { mutableStateOf(authController.getAuthIntent(authService)) }
    val coroutineScope = rememberCoroutineScope()

    // TODO Implement this inside a ViewModel
    val activityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { response ->
            handleAuthResponse(
                response,
                authService,
                coroutineScope,
                authViewModel
            )
        }
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Welcome!",
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
                activityResult.launch(authIntent)
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

private fun handleAuthResponse(
    response: ActivityResult,
    authService: AuthorizationService,
    coroutineScope: CoroutineScope,
    authViewModel: AuthViewModel
) {
    response.data?.let { intent ->
        AuthorizationResponse.fromIntent(intent)?.let { authResponse ->
            authService.performTokenRequest(
                authResponse.createTokenExchangeRequest()
            ) { response, authException ->
                if (response != null) {
                    response.refreshToken?.let { refreshToken ->
                        response.accessToken?.let { accessToken ->
                            coroutineScope.launch {
                                authViewModel.updateEncryptedSharedPreferences(
                                    refreshToken,
                                    accessToken
                                )
                            }
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

@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_6
)
@Composable
private fun SignInPreview() {
    val context = LocalContext.current
    val authViewModel =
        AuthViewModel(
            getSharedPreferences(context),
            NavHostController(LocalContext.current)
        )

    SignInView(authViewModel)
}