package com.asanme.youify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.asanme.youify.model.auth.AuthController
import com.asanme.youify.ui.theme.YouifyTheme
import com.asanme.youify.view.SignInView
import net.openid.appauth.AuthorizationService


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouifyTheme {
                Surface {
                    SignInView()
                }
            }
        }
    }
}