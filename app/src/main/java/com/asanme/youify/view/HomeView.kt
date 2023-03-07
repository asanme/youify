package com.asanme.youify.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
                        videoId = "lJDl4pYWuho",
                        part = "snippet",
                        fields = "items(snippet(title,categoryId),statistics)"
                    )
                }
            }
        ) {
            Text(stringResource(id = R.string.youify_playlist))
        }
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_6,
    uiMode = 1
)
@Composable
private fun preview() {
}