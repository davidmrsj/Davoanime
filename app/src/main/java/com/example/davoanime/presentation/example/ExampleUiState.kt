package com.example.davoanime.presentation.example

import com.example.davoanime.domain.model.ExampleItem

data class ExampleUiState(
    val isLoading: Boolean = false,
    val items: List<ExampleItem> = emptyList(),
    val error: String? = null
)
