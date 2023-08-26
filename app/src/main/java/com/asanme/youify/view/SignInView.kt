package com.asanme.youify.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.asanme.youify.R
import com.asanme.youify.getSharedPreferences
import com.asanme.youify.model.RetrofitHelper
import com.asanme.youify.model.api.YouTubeAPI
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(70f)
        ) {
            Text(
                "Welcome to Youify!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Icon(
                painter = painterResource(id = R.drawable.youify),
                contentDescription = stringResource(id = R.string.youify_icon),
                tint = Color.Unspecified,
                modifier = Modifier.size(250.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(30f)
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                "To get started, Log in using your Google account",
                style = MaterialTheme.typography.displaySmall,
            )

            Divider(thickness = 1.dp, modifier = Modifier.width(100.dp))

            FilledTonalButton(
                onClick = {
                    activityResult.launch(authIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = stringResource(id = R.string.google_icon),
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Log In with Google",
                    style = MaterialTheme.typography.labelSmall
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
            NavHostController(LocalContext.current),
            RetrofitHelper.getInstance().create(YouTubeAPI::class.java)
        )

    SignInView(authViewModel)
}