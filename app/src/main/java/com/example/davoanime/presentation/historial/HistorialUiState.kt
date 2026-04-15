package com.example.davoanime.presentation.historial

import com.example.davoanime.domain.model.WatchProgress

data class HistorialUiState(
    val history: List<WatchProgress> = emptyList(),
    val isLoading: Boolean = true
)
