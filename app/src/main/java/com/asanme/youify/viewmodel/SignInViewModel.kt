package com.asanme.youify.viewmodel

import androidx.lifecycle.ViewModel
import com.asanme.youify.R
import com.asanme.youify.model.YouTubeAPI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SignInViewModel(
    youTubeAPI: YouTubeAPI
): ViewModel() {
    private fun generateCodeChallenge() {

    }
}