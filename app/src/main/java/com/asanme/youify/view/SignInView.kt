package com.asanme.youify.view

import android.content.Context
import android.content.Intent
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
import com.asanme.youify.R
import com.asanme.youify.model.auth.AuthController
import net.openid.appauth.AuthorizationResponse

@Composable
fun SignInView() {
    val context = LocalContext.current
    var text by remember {
        mutableStateOf("default")
    }

    val url = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { response ->
            response.data?.let { data ->
                AuthorizationResponse.fromIntent(data)
            }.run {
                Log.e("NullResponse", "Error")
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
                url.launch(getAuthPermissions(context))
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

private fun getAuthPermissions(context: Context): Intent {
    val jsonConfig =
        context.resources.openRawResource(R.raw.auth_config).bufferedReader().use { it.readText() }
    val authController = AuthController(jsonConfig)
    return authController.getAuthIntent(context)
}

@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_6
)
@Composable
private fun SignInPreview() {
    SignInView()
}