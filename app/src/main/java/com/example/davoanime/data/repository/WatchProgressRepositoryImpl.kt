package com.example.davoanime.data.repository

import com.example.davoanime.data.local.dao.WatchProgressDao
import com.example.davoanime.data.local.entity.WatchProgressEntity
import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchProgressRepositoryImpl @Inject constructor(
    private val dao: WatchProgressDao
) : WatchProgressRepository {

    override fun getContinueWatching(limit: Int): Flow<List<WatchProgress>> {
        return dao.getContinueWatching(limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getWatchHistory(): Flow<List<WatchProgress>> {
        return dao.getWatchHistory().map { list -> list.map { it.toDomain() } }
    }

    override fun getEpisodeProgress(episodeId: Int): Flow<WatchProgress?> {
        return dao.getEpisodeProgress(episodeId).map { it?.toDomain() }
    }

    override suspend fun getEpisodeProgressOnce(episodeId: Int): WatchProgress? {
        return dao.getEpisodeProgressOnce(episodeId)?.toDomain()
    }

    override fun getSeriesProgress(animeId: Int): Flow<List<WatchProgress>> {
        return dao.getSeriesProgress(animeId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveProgress(progress: WatchProgress) {
        dao.upsertProgress(progress.toEntity())
    }

    override suspend fun markAsWatched(episodeId: Int) {
        dao.markAsWatched(episodeId)
    }

    override suspend fun markAsUnwatched(episodeId: Int) {
        dao.markAsUnwatched(episodeId)
    }

    private fun WatchProgressEntity.toDomain(): WatchProgress {
        return WatchProgress(
            episodeId = episodeId,
            animeId = animeId,
            animeTitle = animeTitle,
            episodeTitle = episodeTitle,
            episodeNumber = episodeNumber,
            episodeImage = episodeImage,
            animeImage = animeImage,
            positionMs = positionMs,
            durationMs = durationMs,
            progressPercent = progressPercent,
            isWatched = isWatched,
            updatedAt = updatedAt
        )
    }

    private fun WatchProgress.toEntity(): WatchProgressEntity {
        return WatchProgressEntity(
            episodeId = episodeId,
            animeId = animeId,
            animeTitle = animeTitle,
            episodeTitle = episodeTitle,
            episodeNumber = episodeNumber,
            episodeImage = episodeImage,
            animeImage = animeImage,
            positionMs = positionMs,
            durationMs = durationMs,
            progressPercent = progressPercent,
            isWatched = isWatched,
            updatedAt = updatedAt
        )
    }
}
