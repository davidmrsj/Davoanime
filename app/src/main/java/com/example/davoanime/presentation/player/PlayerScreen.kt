package com.example.davoanime.presentation.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.davoanime.presentation.theme.Background
import com.example.davoanime.presentation.theme.Primary
import com.example.davoanime.presentation.theme.PrimaryContainer
import com.example.davoanime.presentation.theme.PrimaryVariant
import com.example.davoanime.presentation.theme.Secondary
import com.example.davoanime.presentation.theme.Surface
import com.example.davoanime.presentation.theme.SurfaceVariant
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            }
        }

        onDispose {
            activity?.requestedOrientation =
                originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.let { window ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.show(WindowInsets.Type.systemBars())
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando reproductor...",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.error ?: "Error",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::retry) {
                        Text("Reintentar")
                    }
                }
            }
        }

        state.seriesCompleted -> {
            SeriesCompletedOverlay(onBack = { navController.popBackStack() })
        }

        state.streamUrl != null -> {
            VideoPlayer(
                streamUrl = state.streamUrl!!,
                episodeTitle = state.episodeTitle,
                showControls = state.showControls,
                savedPositionMs = state.savedPositionMs,
                showResumeDialog = state.showResumeDialog,
                showNextEpisodeOverlay = state.showNextEpisodeOverlay,
                nextEpisodeCountdown = state.nextEpisodeCountdown,
                nextEpisodeTitle = state.nextEpisodeTitle,
                onToggleControls = viewModel::toggleControls,
                onBackClick = { navController.popBackStack() },
                onPositionUpdate = viewModel::onPositionUpdate,
                onSaveProgressOnExit = viewModel::saveProgressOnExit,
                onSkipToNext = viewModel::skipToNextEpisode,
                onCancelAutoAdvance = viewModel::cancelAutoAdvance,
                onResumeFromSaved = viewModel::resumeFromSaved,
                onRestartEpisode = viewModel::restartEpisode
            )
        }
    }
}

@Composable
fun SeriesCompletedOverlay(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "Serie completada",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Has terminado todos los episodios",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Volver", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    streamUrl: String,
    episodeTitle: String,
    showControls: Boolean,
    savedPositionMs: Long,
    showResumeDialog: Boolean,
    showNextEpisodeOverlay: Boolean,
    nextEpisodeCountdown: Int,
    nextEpisodeTitle: String,
    onToggleControls: () -> Unit,
    onBackClick: () -> Unit,
    onPositionUpdate: (Long, Long) -> Unit,
    onSaveProgressOnExit: (Long, Long) -> Unit,
    onSkipToNext: () -> Unit,
    onCancelAutoAdvance: () -> Unit,
    onResumeFromSaved: () -> Unit,
    onRestartEpisode: () -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var hasSeenkedToSaved by remember { mutableStateOf(false) }

    // Double-tap seek feedback
    var showSeekForward by remember { mutableStateOf(false) }
    var showSeekBackward by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // Cuando cambia la URL del stream (auto-advance)
    LaunchedEffect(streamUrl) {
        hasSeenkedToSaved = false
        exoPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration
                    // Seek a posicion guardada
                    if (!hasSeenkedToSaved && savedPositionMs > 0 && !showResumeDialog) {
                        exoPlayer.seekTo(savedPositionMs)
                        hasSeenkedToSaved = true
                    }
                }
                if(state == Player.STATE_ENDED){
                    onBackClick()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            onSaveProgressOnExit(exoPlayer.currentPosition, exoPlayer.duration)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Seek cuando el usuario elige reanudar
    LaunchedEffect(showResumeDialog) {
        if (!showResumeDialog && savedPositionMs > 0 && !hasSeenkedToSaved) {
            // Esperar a que el player este listo
            while (exoPlayer.playbackState != Player.STATE_READY) {
                delay(100)
            }
            exoPlayer.seekTo(savedPositionMs)
            hasSeenkedToSaved = true
        }
    }

    // Guardar progreso al pausar
    LaunchedEffect(isPlaying) {
        if (!isPlaying && duration > 0 && currentPosition > 30_000) {
            onPositionUpdate(currentPosition, duration)
        }
    }

    // Position updater + guardado periodico cada 10 segundos
    LaunchedEffect(streamUrl) {
        var tickCount = 0
        while (true) {
            if (!isSeeking) {
                currentPosition = exoPlayer.currentPosition
                duration = exoPlayer.duration.coerceAtLeast(1L)
                sliderPosition = currentPosition.toFloat() / duration.toFloat()
            }

            // Reportar posicion al ViewModel cada 10 segundos (20 ticks * 500ms)
            tickCount++
            if (tickCount >= 20 && isPlaying) {
                tickCount = 0
                onPositionUpdate(exoPlayer.currentPosition, exoPlayer.duration)
            }

            delay(500)
        }
    }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls && !isLocked) {
            delay(4000)
            onToggleControls()
        }
    }

    // Double-tap feedback auto-hide
    LaunchedEffect(showSeekForward) {
        if (showSeekForward) { delay(600); showSeekForward = false }
    }
    LaunchedEffect(showSeekBackward) {
        if (showSeekBackward) { delay(600); showSeekBackward = false }
    }

    // Solicitar focus para D-pad
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                // Let D-pad events pass through to dialogs when they are visible
                if (showResumeDialog) return@onKeyEvent false

                // Only react on ACTION_DOWN to avoid double-firing (DOWN + UP)
                if (event.nativeKeyEvent.action != KeyEvent.ACTION_DOWN) return@onKeyEvent true

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_BACK ->{
                        onBackClick()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_MEDIA_REWIND -> {
                        exoPlayer.seekTo((exoPlayer.currentPosition - 10_000).coerceAtLeast(0))
                        showSeekBackward = true
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                        exoPlayer.seekTo((exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration))
                        showSeekForward = true
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (!showControls) onToggleControls()
                        true
                    }
                    else -> false
                }
            }
            .focusable()
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    // Prevent PlayerView from stealing D-pad focus
                    isFocusable = false
                    isFocusableInTouchMode = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gesture layer
        Row(modifier = Modifier.fillMaxSize()) {
            // Left half — double-tap rewind
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(isLocked) {
                        if (!isLocked) {
                            detectTapGestures(
                                onTap = { onToggleControls() },
                                onDoubleTap = {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10_000).coerceAtLeast(0))
                                    showSeekBackward = true
                                }
                            )
                        } else {
                            detectTapGestures(onTap = { onToggleControls() })
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSeekBackward,
                    enter = scaleIn(tween(200)) + fadeIn(tween(200)),
                    exit = scaleOut(tween(300)) + fadeOut(tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(color = Primary.copy(alpha = 0.3f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Replay10, null, tint = Color.White, modifier = Modifier.size(28.dp))
                            Text("-10s", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Right half — double-tap forward
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(isLocked) {
                        if (!isLocked) {
                            detectTapGestures(
                                onTap = { onToggleControls() },
                                onDoubleTap = {
                                    exoPlayer.seekTo((exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration))
                                    showSeekForward = true
                                }
                            )
                        } else {
                            detectTapGestures(onTap = { onToggleControls() })
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSeekForward,
                    enter = scaleIn(tween(200)) + fadeIn(tween(200)),
                    exit = scaleOut(tween(300)) + fadeOut(tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(color = Secondary.copy(alpha = 0.3f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Forward10, null, tint = Color.White, modifier = Modifier.size(28.dp))
                            Text("+10s", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Buffering indicator
        if (isBuffering) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .background(color = Background.copy(alpha = 0.6f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
            }
        }

        // Lock button
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            IconButton(
                onClick = { isLocked = !isLocked },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isLocked) Secondary.copy(alpha = 0.8f) else Surface.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                    contentDescription = if (isLocked) "Desbloquear" else "Bloquear",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Main controls overlay
        AnimatedVisibility(
            visible = showControls && !isLocked,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.75f), Color.Black.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = episodeTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariant.copy(alpha = 0.6f))
                            .clickable { showSpeedSheet = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            color = if (playbackSpeed != 1f) Primary else Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { exoPlayer.seekTo((exoPlayer.currentPosition - 10_000).coerceAtLeast(0)) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Replay10, "Retroceder 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(Primary, PrimaryVariant, Secondary)),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = { exoPlayer.seekTo((exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Forward10, "Avanzar 10s", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Bottom bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.75f))
                            )
                        )
                        .padding(bottom = 8.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatDuration(if (isSeeking) (sliderPosition * duration).toLong() else currentPosition),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "-${formatDuration((duration - if (isSeeking) (sliderPosition * duration).toLong() else currentPosition).coerceAtLeast(0))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Slider(
                        value = sliderPosition,
                        onValueChange = { isSeeking = true; sliderPosition = it },
                        onValueChangeFinished = {
                            isSeeking = false
                            exoPlayer.seekTo((sliderPosition * duration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Secondary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(32.dp)
                    )
                }
            }
        }

        // Overlay: Siguiente episodio
        AnimatedVisibility(
            visible = showNextEpisodeOverlay,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Surface.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Siguiente episodio en ${nextEpisodeCountdown}s",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = nextEpisodeTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSkipToNext,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Filled.SkipNext, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver ahora", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = onCancelAutoAdvance,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Cancelar", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                    }
                }
            }
        }

        // Dialog: Reanudar o empezar de nuevo
        if (showResumeDialog) {
            val resumeFocusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                resumeFocusRequester.requestFocus()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Surface, RoundedCornerShape(20.dp))
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Progreso guardado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tienes progreso en ${formatDuration(savedPositionMs)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Button(
                            onClick = onResumeFromSaved,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(resumeFocusRequester)
                        ) {
                            Text("Continuar desde ${formatDuration(savedPositionMs)}", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onRestartEpisode,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Empezar de nuevo", color = Color.White)
                        }
                    }
                }
            }
        }

        // Speed selector sheet
        if (showSpeedSheet) {
            SpeedSelectorSheet(
                currentSpeed = playbackSpeed,
                onSpeedSelected = { speed ->
                    playbackSpeed = speed
                    exoPlayer.setPlaybackSpeed(speed)
                    showSpeedSheet = false
                },
                onDismiss = { showSpeedSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedSelectorSheet(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val speeds = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Velocidad de reproduccion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            speeds.forEach { speed ->
                val isSelected = speed == currentSpeed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSpeedSelected(speed) }
                        .background(if (isSelected) PrimaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (speed == 1f) "Normal" else "${speed}x",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) Primary else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Box(modifier = Modifier.size(8.dp).background(Primary, CircleShape))
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
