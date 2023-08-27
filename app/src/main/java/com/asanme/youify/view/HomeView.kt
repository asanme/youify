package com.asanme.youify.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.asanme.youify.R
import com.asanme.youify.model.classes.PlaylistRequest
import com.asanme.youify.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.net.URI

@Composable
fun HomeView(authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val videos = authViewModel.userVideos.collectAsState()

    var url by rememberSaveable {
        mutableStateOf("")
    }

    var isError by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { newUrl ->
                isError = false
                url = newUrl
            },
            placeholder = {
                Text(stringResource(id = R.string.enter_url))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.playlist_icon),
                    contentDescription = stringResource(id = R.string.playlist_icon)
                )
            },
            supportingText = {
                if (url.isEmpty()) {
                    Text("Enter a playlist to convert it")
                } else if (isError) {
                    Text("Enter a valid playlist")
                } else {
                    Text("")
                }
            },
            isError = isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ExtendedFloatingActionButton(
            onClick = {
                if (isUrlValid(url)) {
                    val uri = URI(url)
                    val playlistId = uri.findParameterValue("list")

                    // An error appears if the entered text / URL doesn't return the playlistId
                    isError = playlistId == null

                    playlistId?.let {
                        val playlistRequest = PlaylistRequest(
                            playlistId = it,
                            part = "snippet",
                            fields = "pageInfo,nextPageToken,items(snippet(title))",
                            maxResults = 50,
                            videoCategoryId = 10
                        )

                        coroutineScope.launch {
                            authViewModel.getVideoInfo(playlistRequest)
                        }
                    }
                } else {
                    isError = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                painter = painterResource(R.drawable.convert_icon),
                contentDescription = stringResource(id = R.string.convert_icon)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(stringResource(id = R.string.convert_playlist))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videos.value) { video ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        video.snippet.title,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

private fun isUrlValid(url: String) = url.replace(" ", "") != ""

private fun URI.findParameterValue(parameterName: String): String? {
    rawQuery?.let { query ->
        return query.split('&').map {
            val parts = it.split('=')
            val name = parts.firstOrNull() ?: ""
            val value = parts.drop(1).firstOrNull() ?: ""
            Pair(name, value)
        }.firstOrNull { it.first == parameterName }?.second
    }

    return null
}