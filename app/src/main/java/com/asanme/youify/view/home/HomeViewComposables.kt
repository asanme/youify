package com.asanme.youify.view.home

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.asanme.youify.R
import com.asanme.youify.model.classes.PlaylistRequest
import com.asanme.youify.model.classes.VideoSnippet
import com.asanme.youify.model.util.AppConstants.RESULTS_PER_PAGE
import com.asanme.youify.model.util.AppConstants.VIDEO_PROPERTIES
import com.asanme.youify.model.util.findParameterValue
import com.asanme.youify.model.util.isUrlEmpty
import com.asanme.youify.viewmodel.AuthViewModel
import java.net.URI
import java.util.UUID

// TODO Check why the app recomposes when the token is refreshed
// TODO Maybe create more files for the different composable
@Composable
fun PlaylistHeader(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    var url by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }

    Row {
        OutlinedTextField(
            value = url,
            onValueChange = { newUrl ->
                url = newUrl.replace(" ", "")
                isError = false
            },
            placeholder = {
                Text(stringResource(id = R.string.enter_url))
            },
            supportingText = {
                Text(getUrlSupportingText(url, isError))
            },
            isError = isError,
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        ClipboardButton(clipboardManager) { newUrl ->
            url = newUrl
        }
    }

    ExtendedFloatingActionButton(
        onClick = {
            isError = runUrlChecksAndLoadPlaylistInfo(url, authViewModel)
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
}

@Composable
private fun ClipboardButton(
    clipboardManager: ClipboardManager,
    onUrlChanged: (String) -> Unit
) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(10.dp),
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        onClick = {
            onUrlChanged(getClipboardUrl(clipboardManager))
        }
    ) {
        Icon(
            painterResource(R.drawable.paste_icon),
            contentDescription = stringResource(id = R.string.paste_icon)
        )
    }
}

@Composable
fun VideoPreviewer(
    authViewModel: AuthViewModel,
) {
    val lazyListState = rememberLazyListState()
    val videos = authViewModel.videoListFlow.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyListState
    ) {
        items(
            items = videos.value,
            key = {
                it.id = UUID.randomUUID().toString()
                it.id
            },
        ) { videoSnippet ->
            VideoItem(
                video = videoSnippet,
                authViewModel = authViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.VideoItem(
    video: VideoSnippet,
    authViewModel: AuthViewModel
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    authViewModel.removeVideo(video)
                }

                else -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }

            true
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        modifier = Modifier
            .animateItemPlacement()
            .clip(RoundedCornerShape(8.dp)),
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            SwipeBackground(dismissState)
        },
        content = {
            VideoItemCard(video)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection

    if (direction.equals(SwipeToDismissBoxValue.EndToStart)) {
        val color by animateColorAsState(
            targetValue = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.Settled -> Color.LightGray
                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> Color.Red
            },
            label = "ColorAnimation"
        )

        val scale by animateFloatAsState(
            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
            label = "ScaleIconAnimation"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.garbage_icon),
                modifier = Modifier.scale(scale)
            )
        }
    }
}

@Composable
private fun VideoItemCard(video: VideoSnippet) {
    Card(
        modifier = Modifier
            .height(120.dp),
    ) {
        val highResUrl = video.snippet.thumbnail?.high?.url
        val maxResUrl = video.snippet.thumbnail?.maxRes?.url

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
        ) {
            ResolveVideoThumbnail(
                maxResUrl,
                highResUrl
            )

            Text(
                video.snippet.title,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

// Loads the video thumbnail depending on the resolution
@Composable
private fun RowScope.ResolveVideoThumbnail(
    maxResUrl: String?,
    highResUrl: String?
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
    } else if (highResUrl != null) {
        AsyncImage(
            model = highResUrl,
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
}

private fun getClipboardUrl(
    clipboardManager: ClipboardManager
): String {
    var url = ""
    val clipboardCharSequence = clipboardManager.primaryClip?.getItemAt(0)?.text
    val clipboardText = clipboardCharSequence.toString()

    // Specific case where the copied line contains a newline (U+000A)
    if (clipboardText.isNotEmpty() && !clipboardText.contains('\u000A')) {
        url = clipboardText.replace(" ", "")
    }

    Log.e("ClipboardManager", url)

    return url
}

private fun getUrlSupportingText(
    url: String,
    isError: Boolean
): String {
    return if (url.isEmpty()) {
        "Enter a playlist to convert it"
    } else if (isError) {
        "Enter a valid playlist"
    } else {
        "Press convert playlist"
    }
}

private fun runUrlChecksAndLoadPlaylistInfo(
    url: String,
    authViewModel: AuthViewModel
): Boolean {
    val isError: Boolean
    if (!isUrlEmpty(url)) {
        val uri = URI(url.replace(" ", ""))
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

            authViewModel.clearVideos()
            authViewModel.getVideoInfo(playlistRequest)
        }
    } else {
        isError = true
    }

    return isError
}
