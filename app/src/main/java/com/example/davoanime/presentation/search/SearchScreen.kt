package com.example.davoanime.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.davoanime.R
import com.example.davoanime.domain.model.SearchFilters
import com.example.davoanime.presentation.components.SearchAnimeListItem
import com.example.davoanime.presentation.components.SearchTopBar
import com.example.davoanime.presentation.navigation.Screen
import com.example.davoanime.presentation.theme.Primary
import com.example.davoanime.presentation.theme.Secondary
import com.example.davoanime.presentation.util.isTv

@Composable
fun SearchScreen(
    navController: NavController? = null,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= state.searchResults.size - 6 &&
                    state.currentPage < state.lastPage &&
                    !state.isLoadingMore
        }
    }

    if (shouldLoadMore) {
        viewModel.loadMore()
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onFilterClick = viewModel::toggleFilterSheet
            )
        }
    ) { paddingValues ->
        Box(
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
                state.isEmpty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Busca tu anime favorito",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(if(isTv(LocalContext.current)) 5 else 2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = dimensionResource(id = R.dimen.spacing_16),
                            end = dimensionResource(id = R.dimen.spacing_16),
                            top = dimensionResource(id = R.dimen.spacing_8),
                            bottom = 10.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_12))
                    ) {
                        items(state.searchResults, key = { it.id }) { anime ->
                            SearchAnimeListItem(
                                anime = anime,
                                onClick = {
                                    navController?.navigate(Screen.Detail.createRoute(anime.id, anime.image))
                                }
                            )
                        }

                        if (state.isLoadingMore) {
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

    // Filter BottomSheet
    if (state.isFilterSheetVisible) {
        FilterBottomSheet(
            filters = state.filters,
            onFilterChange = viewModel::onFilterChange,
            onApply = viewModel::applyFilters,
            onClear = viewModel::clearFilters,
            onDismiss = viewModel::toggleFilterSheet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filters: SearchFilters,
    onFilterChange: (SearchFilters) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_16),
                    vertical = dimensionResource(id = R.dimen.spacing_8)
                )
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onClear) {
                    Text(
                        text = "Limpiar",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))

            // Ordenar por
            FilterSection(
                title = "Ordenar por",
                options = listOf(
                    "Por defecto" to "",
                    "Popularidad" to "popularidad",
                    "Nombre" to "nombre"
                ),
                selectedValue = filters.filtro,
                onSelect = { onFilterChange(filters.copy(filtro = it)) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))

            // Tipo
            FilterSection(
                title = "Tipo",
                options = listOf(
                    "Todos" to "none",
                    "TV" to "TV",
                    "Película" to "Movie"
                ),
                selectedValue = filters.tipo,
                onSelect = { onFilterChange(filters.copy(tipo = it)) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))

            // Estado
            FilterSection(
                title = "Estado",
                options = listOf(
                    "Todos" to "none",
                    "En emisión" to "currently",
                    "Finalizado" to "finished",
                    "Próximamente" to "notyet"
                ),
                selectedValue = filters.estado,
                onSelect = { onFilterChange(filters.copy(estado = it)) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))

            // Orden
            FilterSection(
                title = "Orden",
                options = listOf(
                    "Por defecto" to "",
                    "Descendente" to "desc",
                    "Ascendente" to "asc"
                ),
                selectedValue = filters.orden,
                onSelect = { onFilterChange(filters.copy(orden = it)) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_16)))

            // Año
            FilterSection(
                title = "Año",
                options = listOf(
                    "Todos" to "",
                    "2026" to "2026",
                    "2025" to "2025",
                    "2024" to "2024",
                    "2023" to "2023",
                    "2022" to "2022"
                ),
                selectedValue = filters.fecha,
                onSelect = { onFilterChange(filters.copy(fecha = it)) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_24)))

            // Apply button
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
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
                        .padding(vertical = dimensionResource(id = R.dimen.spacing_12)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aplicar Filtros",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_32)))
        }
    }
}

@Composable
fun FilterSection(
    title: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_8))
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (label, value) ->
                FilterChip(
                    selected = selectedValue == value,
                    onClick = { onSelect(value) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.Transparent,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        enabled = true,
                        selected = selectedValue == value
                    )
                )
            }
        }
    }
}
