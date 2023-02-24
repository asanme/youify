package com.asanme.youify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.asanme.youify.ui.theme.YouifyTheme
import com.asanme.youify.view.MainView
import com.asanme.youify.view.SignInView

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
    SignInView()
}