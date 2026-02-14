package com.example.davoanime.presentation.example

import com.example.davoanime.domain.model.HorarioAnime
import com.example.davoanime.domain.model.Reciente

data class ExampleUiState(
    val isLoading: Boolean = false,
    val items: List<Reciente> = emptyList(),
    val todayAnimes: List<HorarioAnime> = emptyList(),
    val todayName: String = "",
    val error: String? = null
)
