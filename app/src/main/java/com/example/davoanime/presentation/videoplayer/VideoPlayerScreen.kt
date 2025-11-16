@file:OptIn(UnstableApi::class)

package com.example.davoanime.presentation.videoplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private const val SAMPLE_URL = "https://nika.playmudos.com/TWlKN2tqZUt1WEZWdEdhKzl2c2Fzci9xeFA4Q0NhanVCQnRad21pRzErUVl3UGsvT2FWUTJvd2RqVUJFM0NLMw.m3u8?st=A4RfHUjLvsIVSYbbUVl45Q&e=1763267589"

@Composable
fun VideoPlayerScreen(modifier: Modifier = Modifier) {
    MaterialTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            VideoPlayerContent()
        }
    }
}

@Composable
private fun VideoPlayerContent() {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(SAMPLE_URL)
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
        }
    }
    var showPlayer by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.pause()
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(if (showPlayer) Alignment.TopCenter else Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = if (showPlayer) Arrangement.Top else Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!showPlayer) {
                        showPlayer = true
                    }
                    exoPlayer.seekToDefaultPosition()
                    exoPlayer.play()
                },
                modifier = Modifier.semantics {
                    contentDescription = "Start video playback"
                }
            ) {
                Text(text = "Start")
            }

            if (showPlayer) {
                Spacer(modifier = Modifier.height(16.dp))
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(16f / 9f)
                        .semantics { contentDescription = "Media player" },
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = true
                            player = exoPlayer
                        }
                    }
                )
            }
        }
    }
}
