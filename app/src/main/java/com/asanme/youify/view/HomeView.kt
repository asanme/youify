package com.asanme.youify.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.asanme.youify.R
import com.asanme.youify.model.classes.PlaylistRequest
import com.asanme.youify.model.classes.VideoSnippet
import com.asanme.youify.model.misc.AppConstants.RESULTS_PER_PAGE
import com.asanme.youify.model.misc.AppConstants.VIDEO_PROPERTIES
import com.asanme.youify.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
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
                            fields = VIDEO_PROPERTIES,
                            maxResults = RESULTS_PER_PAGE,
                            videoCategoryId = 10,
                            pageToken = null
                        )

                        coroutineScope.launch {
                            authViewModel.clearVideos()
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

        VideoPreviewer(videos, authViewModel, coroutineScope)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun VideoPreviewer(
    videos: State<List<VideoSnippet>>,
    authViewModel: AuthViewModel,
    coroutineScope: CoroutineScope
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(videos.value) { video ->
            val currentItem = rememberUpdatedState(video)
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) {
                        coroutineScope.launch {
                            authViewModel.removeVideo(currentItem.value)
                        }
                    }

                    true
                },
                positionalThreshold = { swipeActivationFloat -> swipeActivationFloat / 3 }
            )

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                modifier = Modifier.animateItemPlacement(),
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.LightGray
                            DismissValue.DismissedToEnd -> Color.Red
                            DismissValue.DismissedToStart -> Color.Red
                        },
                        label = ""
                    )

                    val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

                    val alignment = when (direction) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                    }

                    val icon = when (direction) {
                        DismissDirection.StartToEnd -> Icons.Default.Delete
                        DismissDirection.EndToStart -> Icons.Default.Delete
                    }

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                        label = ""
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = "Localized description",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    VideoItemCard(video)
                }
            )
        }
    }
}

@Composable
private fun VideoItemCard(video: VideoSnippet) {
    Card(
        modifier = Modifier.height(120.dp),
    ) {
        val maxResUrl = video.snippet.thumbnail?.maxRes?.url
        val highUrl = video.snippet.thumbnail?.high?.url

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
        ) {
            if (maxResUrl != null) {
                AsyncImage(
                    model = maxResUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxHeight()
                        .weight(1f)
                )
            } else if (highUrl != null) {
                AsyncImage(
                    model = highUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.hidden_icon),
                    contentDescription = stringResource(id = R.string.hidden_icon),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }

            Text(
                video.snippet.title,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
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