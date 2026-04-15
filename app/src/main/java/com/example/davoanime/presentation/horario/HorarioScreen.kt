package com.example.davoanime.presentation.horario

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.davoanime.domain.model.HorarioAnime
import com.example.davoanime.presentation.navigation.Screen

@Composable
fun HorarioScreen(
    navController: NavController,
    viewModel: HorarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HorarioContent(
        state = state,
        onDaySelected = viewModel::selectDay,
        onRetry = viewModel::retry,
        onAnimeClick = { anime ->
            navController.navigate(Screen.Detail.createRoute(anime.id, anime.image))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioContent(
    state: HorarioUiState,
    onDaySelected: (String) -> Unit,
    onRetry: () -> Unit,
    onAnimeClick: (HorarioAnime) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Horarios",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.spacing_16)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))
                            Button(onClick = onRetry) {
                                Text(text = "Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    val sortedDays = state.dayNames.entries.sortedBy { it.key.toIntOrNull() ?: 0 }
                    val selectedIndex = sortedDays.indexOfFirst { it.key == state.selectedDay }.coerceAtLeast(0)

                    if (sortedDays.isNotEmpty()) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedIndex,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            edgePadding = dimensionResource(id = R.dimen.spacing_16),
                            divider = {}
                        ) {
                            sortedDays.forEachIndexed { index, (dayKey, dayName) ->
                                Tab(
                                    selected = index == selectedIndex,
                                    onClick = { onDaySelected(dayKey) },
                                    text = {
                                        Text(
                                            text = dayName,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                                            color = if (index == selectedIndex)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_8)))

                    val animesForDay = state.schedule[state.selectedDay] ?: emptyList()

                    if (animesForDay.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay anime programado para este día",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = dimensionResource(id = R.dimen.spacing_16),
                                end = dimensionResource(id = R.dimen.spacing_16),
                                bottom = 10.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12))
                        ) {
                            items(animesForDay, key = { it.id }) { anime ->
                                HorarioAnimeCard(
                                    anime = anime,
                                    onClick = { onAnimeClick(anime) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorarioAnimeCard(
    anime: HorarioAnime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_16)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_12)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(anime.image)
                    .crossfade(true)
                    .build(),
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_12)))
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_12)))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_4)))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = anime.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.spacing_4)))
                    )

                    Text(
                        text = "·",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = anime.estado,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (anime.ultimo != null) {
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_4)))
                    Text(
                        text = "Último: EP ${anime.ultimo.number}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
