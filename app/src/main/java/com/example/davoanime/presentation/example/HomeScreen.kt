package com.example.davoanime.presentation.example

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.davoanime.domain.model.HorarioAnime
import com.example.davoanime.domain.model.Reciente
import com.example.davoanime.presentation.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController? = null,
    viewModel: ExampleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onRecienteClick = { reciente ->
            navController?.navigate(Screen.Detail.createRoute(reciente.animeId))
        },
        onTodayAnimeClick = { anime ->
            navController?.navigate(Screen.Detail.createRoute(anime.id))
        },
        onRetry = viewModel::retry
    )
}

@Composable
fun HomeContent(
    state: ExampleUiState,
    onRecienteClick: (Reciente) -> Unit,
    onTodayAnimeClick: (HorarioAnime) -> Unit,
    onRetry: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val recentCardWidth = (screenWidth - 16.dp * 2 - 12.dp) / 2.3f
    val todayCardWidth = (screenWidth - 16.dp * 2 - 12.dp) / 2.5f

    Scaffold { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(dimensionResource(id = R.dimen.spacing_16)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Error",
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    contentPadding = PaddingValues(bottom = 10.dp)
                ) {
                    // App Title
                    item {
                        Text(
                            text = "DavoAnime",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(id = R.dimen.spacing_16),
                                vertical = dimensionResource(id = R.dimen.spacing_12)
                            )
                        )
                    }

                    // Section: Últimos Episodios
                    item {
                        SectionHeader(title = "Últimos Episodios")
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = dimensionResource(id = R.dimen.spacing_16)
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(id = R.dimen.spacing_12)
                            )
                        ) {
                            items(state.items, key = { it.id }) { reciente ->
                                RecentEpisodeCard(
                                    reciente = reciente,
                                    cardWidth = recentCardWidth,
                                    onClick = { onRecienteClick(reciente) }
                                )
                            }
                        }
                    }

                    // Section: Emitiendo Hoy
                    if (state.todayAnimes.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_8)))
                            SectionHeader(
                                title = "Emitiendo Hoy · ${state.todayName}"
                            )
                        }

                        item {
                            LazyRow(
                                contentPadding = PaddingValues(
                                    horizontal = dimensionResource(id = R.dimen.spacing_16)
                                ),
                                horizontalArrangement = Arrangement.spacedBy(
                                    dimensionResource(id = R.dimen.spacing_12)
                                )
                            ) {
                                items(state.todayAnimes, key = { it.id }) { anime ->
                                    TodayAnimeCard(
                                        anime = anime,
                                        cardWidth = todayCardWidth,
                                        onClick = { onTodayAnimeClick(anime) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(
            horizontal = dimensionResource(id = R.dimen.spacing_16),
            vertical = dimensionResource(id = R.dimen.spacing_4)
        )
    )
}

@Composable
fun RecentEpisodeCard(
    reciente: Reciente,
    cardWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(cardWidth)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_16)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2))
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(reciente.animeImage.ifBlank { reciente.image })
                    .crossfade(true)
                    .build(),
                contentDescription = reciente.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.radius_16),
                            topEnd = dimensionResource(id = R.dimen.radius_16)
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_8))
            ) {
                Text(
                    text = reciente.animeTitle.ifBlank { reciente.title },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_2)))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "EP ${reciente.number}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    if (reciente.animeType.isNotBlank()) {
                        Text(
                            text = reciente.animeType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayAnimeCard(
    anime: HorarioAnime,
    cardWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(cardWidth)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_16)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2))
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(anime.image)
                    .crossfade(true)
                    .build(),
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.radius_16),
                            topEnd = dimensionResource(id = R.dimen.radius_16)
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_8))
            ) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (anime.ultimo != null) {
                    Text(
                        text = "EP ${anime.ultimo.number}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}