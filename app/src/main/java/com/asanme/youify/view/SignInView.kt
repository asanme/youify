package com.asanme.youify.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.core.content.ContextCompat.startActivity
import com.asanme.youify.R


@Composable
fun SignInView() {
    val context = LocalContext.current
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
                 getAuthPermissions(context)
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

private fun getAuthPermissions(context: Context) {
    val url =
        "https://accounts.google.com/o/oauth2/v2/auth?client_id=629936952678-lbq4hkcn2p14r38844pa65d21rspuaie.apps.googleusercontent.com&redirect_uri=com.asanme.youify/oauth2redirect&response_type=code&scope=https://www.googleapis.com/auth/youtube"
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(context, i, null)
}

@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_6
)
@Composable
private fun SignInPreview() {
    SignInView()
}