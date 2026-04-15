package com.example.davoanime.presentation.detail

import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.model.WatchProgress

data class AnimeDetailUiState(
    val isLoading: Boolean = false,
    val animeDetail: AnimeDetail? = null,
    val episodes: List<Episode> = emptyList(),
    val error: String? = null,
    val isSynopsisExpanded: Boolean = false,
    val selectedTabIndex: Int = 0,
    val episodeProgress: Map<Int, WatchProgress> = emptyMap(),
    val imageUrl: String? = null
)
