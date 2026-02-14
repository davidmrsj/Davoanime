package com.example.davoanime.presentation.player

data class PlayerUiState(
    val isLoading: Boolean = false,
    val streamUrl: String? = null,
    val error: String? = null,
    val isPlaying: Boolean = true,
    val showControls: Boolean = true,
    val episodeTitle: String = ""
)
