package com.example.davoanime.presentation.episodes

import com.example.davoanime.domain.model.Episode

data class EpisodesUiState(
    val isLoading: Boolean = false,
    val displayedEpisodes: List<Episode> = emptyList(),
    val allEpisodesCount: Int = 0,
    val hasMore: Boolean = false,
    val animeTitle: String = "",
    val error: String? = null
)
