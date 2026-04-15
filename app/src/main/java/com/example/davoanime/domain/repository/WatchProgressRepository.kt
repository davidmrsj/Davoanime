package com.example.davoanime.domain.repository

import com.example.davoanime.domain.model.WatchProgress
import kotlinx.coroutines.flow.Flow

interface WatchProgressRepository {
    fun getContinueWatching(limit: Int = 10): Flow<List<WatchProgress>>
    fun getWatchHistory(): Flow<List<WatchProgress>>
    fun getEpisodeProgress(episodeId: Int): Flow<WatchProgress?>
    suspend fun getEpisodeProgressOnce(episodeId: Int): WatchProgress?
    fun getSeriesProgress(animeId: Int): Flow<List<WatchProgress>>
    suspend fun saveProgress(progress: WatchProgress)
    suspend fun markAsWatched(episodeId: Int)
    suspend fun markAsUnwatched(episodeId: Int)
}
