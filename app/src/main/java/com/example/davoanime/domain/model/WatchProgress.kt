package com.example.davoanime.domain.model

data class WatchProgress(
    val episodeId: Int,
    val animeId: Int,
    val animeTitle: String,
    val episodeTitle: String,
    val episodeNumber: Int,
    val episodeImage: String,
    val animeImage: String = "",
    val positionMs: Long,
    val durationMs: Long,
    val progressPercent: Float,
    val isWatched: Boolean,
    val updatedAt: Long
)
