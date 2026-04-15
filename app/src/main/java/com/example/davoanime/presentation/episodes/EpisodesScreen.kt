package com.example.davoanime.presentation.episodes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.davoanime.R
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.presentation.navigation.Screen

@Composable
fun EpisodesScreen(
    navController: NavController,
    viewModel: EpisodesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EpisodesContent(
        state = state,
        onBackClick = { navController.popBackStack() },
        onEpisodeClick = { episode ->
            navController.navigate(Screen.Player.createRoute(episode.id, episode.animeId))
        },
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry,
        onToggleWatched = viewModel::toggleWatched
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesContent(
    state: EpisodesUiState,
    onBackClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onToggleWatched: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            // Trigger cuando estamos cerca del final (menos de 5 items restantes)
            // Verificamos totalItems > 0 para evitar llamadas iniciales innecesarias
            totalItems > 0 && 
            lastVisibleItemIndex >= (totalItems - 5) && 
            state.hasMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.animeTitle.ifBlank { "Episodios" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
                        Button(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = dimensionResource(id = R.dimen.spacing_16),
                        end = dimensionResource(id = R.dimen.spacing_16),
                        bottom = dimensionResource(id = R.dimen.spacing_16)
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8))
                ) {
                    item {
                        Text(
                            text = "${state.allEpisodesCount} episodios",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                vertical = dimensionResource(id = R.dimen.spacing_8)
                            )
                        )
                    }

                    items(state.displayedEpisodes, key = { it.id }) { episode ->
                        EpisodeCard(
                            episode = episode,
                            progress = state.episodeProgress[episode.id],
                            onClick = { onEpisodeClick(episode) },
                            onLongClick = { onToggleWatched(episode.id) }
                        )
                    }

                    if (state.hasMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.spacing_16)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EpisodeCard(
    episode: Episode,
    progress: WatchProgress? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_1))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.spacing_12)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(episode.image.ifEmpty { "https://via.placeholder.com/320x180/1a1a1a/666666?text=Episodio+${episode.number}" })
                            .crossfade(true)
                            .error(android.R.drawable.ic_menu_gallery)
                            .build(),
                        contentDescription = episode.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(140.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_8)))
                    )

                    // Mini progress bar on thumbnail
                    if (progress != null && !progress.isWatched && progress.progressPercent > 0f) {
                        LinearProgressIndicator(
                            progress = { progress.progressPercent },
                            modifier = Modifier
                                .width(140.dp)
                                .height(3.dp)
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(bottomStart = dimensionResource(id = R.dimen.radius_8), bottomEnd = dimensionResource(id = R.dimen.radius_8))),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_12)))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Episodio ${episode.number}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_4)))

                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (progress?.isWatched == true) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Visto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_4)))
                }

                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
