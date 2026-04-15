package com.example.davoanime.presentation.player

data class PlayerUiState(
    val isLoading: Boolean = false,
    val streamUrl: String? = null,
    val error: String? = null,
    val isPlaying: Boolean = true,
    val showControls: Boolean = true,
    val episodeTitle: String = "",
    // Progreso guardado
    val savedPositionMs: Long = 0L,
    val showResumeDialog: Boolean = false,
    // Auto-avance
    val showNextEpisodeOverlay: Boolean = false,
    val nextEpisodeCountdown: Int = 5,
    val nextEpisodeTitle: String = "",
    val isLastEpisode: Boolean = false,
    val seriesCompleted: Boolean = false,
    // Metadata del episodio actual
    val currentEpisodeId: Int = 0,
    val currentAnimeId: Int = 0,
    val currentEpisodeNumber: Int = 0
)
