package com.example.davoanime.presentation.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.davoanime.presentation.components.AnimeListItem

@Composable
fun HomeScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ExampleContent(state = state)
}

@Composable
fun ExampleContent(state: ExampleUiState) {
    Surface(modifier = Modifier.fillMaxSize()) {
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
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.error ?: "", style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(state.items) { item ->
                        AnimeListItem(modifier = Modifier.fillMaxSize(), anime = item, onclick = {})
                    }
                }
            }
        }
    }

    Text(
        text = "Hola mundo",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview
@Composable
private fun ExampleContentPreviewLoading() {
    ExampleContent(
        state = ExampleUiState(isLoading = true)
    )
}

@Preview
@Composable
private fun ExampleContentPreviewData() {
    ExampleContent(
        state = ExampleUiState(
            items = listOf(
                 //ExampleItem(id = "1", title = "Example A"),
                 //ExampleItem(id = "2", title = "Example B")
            )
        )
    )
}
