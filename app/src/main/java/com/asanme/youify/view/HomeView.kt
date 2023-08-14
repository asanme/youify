package com.asanme.youify.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.asanme.youify.R
import com.asanme.youify.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeView(authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var url by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        TextField(
            value = url,
            onValueChange = { newUrl ->
                url = newUrl
            },
            label = {
                Text(stringResource(id = R.string.enter_url))
            }
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    authViewModel.getVideoInfo(
                        playlistId = "PLeySRPnY35dFSDPi_4Q5R1VCGL_pab26A",
                        part = "snippet",
                        fields = "pageInfo,nextPageToken,items(snippet(title))",
                        maxResults = 50,
                        videoCategoryId = 10
                    )
                }
            }
        ) {
            Text(stringResource(id = R.string.youify_playlist))
        }
    }
}