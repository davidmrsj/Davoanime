package com.example.davoanime.presentation.horario

import com.example.davoanime.domain.model.HorarioAnime

data class HorarioUiState(
    val isLoading: Boolean = false,
    val schedule: Map<String, List<HorarioAnime>> = emptyMap(),
    val dayNames: Map<String, String> = emptyMap(),
    val selectedDay: String = "",
    val error: String? = null
)
