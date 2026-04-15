package com.example.davoanime.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.model.RelatedAnime
import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.presentation.navigation.Screen
import com.example.davoanime.presentation.theme.Primary
import com.example.davoanime.presentation.theme.Secondary

@Composable
fun AnimeDetailScreen(
    navController: NavController,
    viewModel: AnimeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
                        Button(onClick = viewModel::retry) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            state.animeDetail != null -> {
                AnimeDetailContent(
                    detail = state.animeDetail!!,
                    episodes = state.episodes,
                    episodeProgress = state.episodeProgress,
                    isSynopsisExpanded = state.isSynopsisExpanded,
                    selectedTabIndex = state.selectedTabIndex,
                    onToggleSynopsis = viewModel::toggleSynopsis,
                    onTabSelected = viewModel::selectTab,
                    onBackClick = { navController.popBackStack() },
                    onWatchClick = {
                        state.animeDetail?.let { detail ->
                            navController.navigate(Screen.Episodes.createRoute(detail.id))
                        }
                    },
                    onEpisodeClick = { episode ->
                        navController.navigate(
                            Screen.Player.createRoute(
                                episode.id,
                                state.animeDetail!!.id
                            )
                        )
                    },
                    onAllEpisodesClick = {
                        navController.navigate(Screen.Episodes.createRoute(state.animeDetail!!.id))
                    },
                    onRelatedClick = { related ->
                        navController.navigate(Screen.Detail.createRoute(related.id, related.image))
                    },
                    paddingValues = paddingValues,
                    imageUrl = state.imageUrl
                )
            }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailContent(
    detail: AnimeDetail,
    episodes: List<Episode>,
    episodeProgress: Map<Int, WatchProgress> = emptyMap(),
    isSynopsisExpanded: Boolean,
    selectedTabIndex: Int,
    onToggleSynopsis: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onWatchClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    onAllEpisodesClick: () -> Unit,
    onRelatedClick: (RelatedAnime) -> Unit,
    paddingValues: PaddingValues,
    imageUrl: String?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = paddingValues
    ) {
        // Hero Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = detail.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.spacing_16))
                        .align(Alignment.TopStart)
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (detail.tipo.isNotEmpty()) {
                    // Type badge
                    Box(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.spacing_16))
                            .align(Alignment.TopEnd)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_4))
                            )
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.spacing_8),
                                vertical = dimensionResource(id = R.dimen.spacing_4)
                            )
                    ) {
                        Text(
                            text = detail.tipo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Studio
        item {
            if (detail.estudios.isNotEmpty()) {
                Text(
                    text = detail.estudios.joinToString(" · ").uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.spacing_16),
                        vertical = dimensionResource(id = R.dimen.spacing_4)
                    )
                )
            }
        }

        // Title
        item {
            Text(
                text = detail.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_16),
                    vertical = dimensionResource(id = R.dimen.spacing_4)
                )
            )
        }

        // Metadata row with rating stars
        item {
            Row(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_16),
                    vertical = dimensionResource(id = R.dimen.spacing_4)
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8))
            ) {
                // Rating stars
                val rating = (detail.votos / 1000.0).coerceIn(0.0, 5.0)
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (index < rating.toInt()) Color(0xFFFFD700) else Color(0xFF424242),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)

                Text(
                    text = detail.temporada,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)

                Text(
                    text = detail.estado,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action buttons
        item {
            Row(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_16),
                    vertical = dimensionResource(id = R.dimen.spacing_8)
                ),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onWatchClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(listOf(Primary, Secondary)),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.spacing_24),
                                vertical = dimensionResource(id = R.dimen.spacing_12)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Watch",
                                tint = Color.White
                            )
                            Text(
                                text = "Ver",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Synopsis
        item {
            Column(
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_16),
                    vertical = dimensionResource(id = R.dimen.spacing_8)
                )
            ) {
                Text(
                    text = "Synopsis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_8)))

                Text(
                    text = detail.synopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (isSynopsisExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis
                )

                if (detail.synopsis.length > 200) {
                    Text(
                        text = if (isSynopsisExpanded) "Leer menos" else "Leer más",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(onClick = onToggleSynopsis)
                            .padding(vertical = dimensionResource(id = R.dimen.spacing_4))
                    )
                }
            }
        }

        // Tab Row
        item {
            val tabs = listOf("Episodios", "Info")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Tab content
        when (selectedTabIndex) {
            0 -> {
                // Episodes tab
                val previewEpisodes = episodes.take(5)
                items(previewEpisodes, key = { it.id }) { episode ->
                    EpisodePreviewCard(
                        episode = episode,
                        isWatched = episodeProgress[episode.id]?.isWatched == true,
                        onClick = { onEpisodeClick(episode) }
                    )
                }

                if (episodes.size > 5) {
                    item {
                        Button(
                            onClick = onAllEpisodesClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.spacing_16)),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Ver todos los episodios (${episodes.size}) →",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (episodes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cargando episodios...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            1 -> {
                // Info tab
                item {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_16)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12))
                    ) {
                        // Anime Image
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(detail.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = detail.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)))
                        )

                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_4)))

                        // Tags
                        if (detail.tags.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "GÉNEROS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_8)))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        dimensionResource(
                                            id = R.dimen.spacing_8
                                        )
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8))
                                ) {
                                    detail.tags.forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_8))
                                                )
                                                .padding(
                                                    horizontal = dimensionResource(id = R.dimen.spacing_12),
                                                    vertical = dimensionResource(id = R.dimen.spacing_8)
                                                )
                                        ) {
                                            Text(
                                                text = tag.replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Type
                        InfoRow(label = "TIPO", value = detail.tipo)

                        // Status
                        InfoRow(label = "ESTADO", value = detail.estado)

                        // Season
                        if (detail.temporada.isNotBlank()) {
                            InfoRow(label = "TEMPORADA", value = detail.temporada)
                        }

                        // Studios
                        if (detail.estudios.isNotEmpty()) {
                            InfoRow(label = "ESTUDIO", value = detail.estudios.joinToString(", "))
                        }

                        // Rating
                        val rating = (detail.votos / 1000.0).coerceIn(0.0, 5.0)
                        Column {
                            Text(
                                text = "VALORACIÓN",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_4)))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (index < rating.toInt()) Color(0xFFFFD700) else Color(
                                            0xFF424242
                                        ),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = String.format("%.1f/5.0", rating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // Votes
                        InfoRow(label = "VOTOS", value = formatVotes(detail.votos))
                    }
                }
            }
        }

        // Related Seasons
        if (detail.temporadas.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
                Text(
                    text = "Temporadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.spacing_16))
                )
            }
            detail.temporadas.forEach { (category, animes) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.spacing_16),
                            vertical = dimensionResource(id = R.dimen.spacing_4)
                        )
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.spacing_16)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12))
                    ) {
                        items(animes, key = { it.id }) { related ->
                            RelatedAnimeCard(
                                related = related,
                                onClick = { onRelatedClick(related) })
                        }
                    }
                }
            }
        }

        // Related Anime
        if (detail.relacionados.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
                Text(
                    text = "Relacionados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.spacing_16))
                )
            }
            detail.relacionados.forEach { (category, animes) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.spacing_16),
                            vertical = dimensionResource(id = R.dimen.spacing_4)
                        )
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.spacing_16)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12))
                    ) {
                        items(animes, key = { it.id }) { related ->
                            RelatedAnimeCard(
                                related = related,
                                onClick = { onRelatedClick(related) })
                        }
                    }
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
        }
    }
}

@Composable
fun EpisodePreviewCard(
    episode: Episode,
    isWatched: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.spacing_16),
                vertical = dimensionResource(id = R.dimen.spacing_4)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_8)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(episode.image.ifEmpty { "https://via.placeholder.com/320x180/1a1a1a/666666?text=Episodio+${episode.number}" })
                    .crossfade(true)
                    .error(android.R.drawable.ic_menu_gallery)
                    .build(),
                contentDescription = episode.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_8)))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_12)))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Episodio ${episode.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isWatched) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "Visto",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_4)))
            }

            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun RelatedAnimeCard(
    related: RelatedAnime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2))
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(related.image)
                    .crossfade(true)
                    .build(),
                contentDescription = related.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.radius_12),
                            topEnd = dimensionResource(id = R.dimen.radius_12)
                        )
                    )
            )

            Text(
                text = related.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_8))
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_2)))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun formatVotes(votes: Int): String {
    return when {
        votes >= 1_000_000 -> String.format("%.1fM", votes / 1_000_000.0)
        votes >= 1_000 -> String.format("%.1fK", votes / 1_000.0)
        else -> votes.toString()
    }
}
