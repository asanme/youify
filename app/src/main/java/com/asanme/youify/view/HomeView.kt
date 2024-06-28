package com.asanme.youify.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asanme.youify.view.home.PlaylistHeader
import com.asanme.youify.view.home.VideoPreviewer
import com.asanme.youify.viewmodel.AuthViewModel

// TODO Implement Dependency Injection pattern to access ViewModel
@Composable
fun HomeView(authViewModel: AuthViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
    ) {
        HomeViewContent(authViewModel)
    }
}

@Composable
private fun HomeViewContent(authViewModel: AuthViewModel) {
    PlaylistHeader(authViewModel)
    VideoPreviewer(authViewModel)
}