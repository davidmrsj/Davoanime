package com.example.davoanime.presentation.util

import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.domain.repository.WatchProgressRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class WatchProgressManager @Inject constructor(
    private val repository: WatchProgressRepository
) {
    private var lastSavedPositionMs: Long = -1L
    private var currentContext: EpisodeContext? = null

    data class EpisodeContext(
        val episodeId: Int,
        val animeId: Int,
        val animeTitle: String,
        val episodeTitle: String,
        val episodeNumber: Int,
        val episodeImage: String,
        val animeImage: String = ""
    )

    fun setEpisodeContext(context: EpisodeContext) {
        currentContext = context
        lastSavedPositionMs = -1L
    }

    suspend fun onPositionUpdate(positionMs: Long, durationMs: Long) {
        val ctx = currentContext ?: return
        if (durationMs <= 0) return

        // No guardar si se ha visto menos de 30 segundos
        if (positionMs < 30_000) return

        // Solo guardar si la posicion cambio mas de 5 segundos
        if (lastSavedPositionMs >= 0 && abs(positionMs - lastSavedPositionMs) < 5_000) return

        val progress = positionMs.toFloat() / durationMs.toFloat()
        val isWatched = shouldMarkAsWatched(positionMs, durationMs)

        repository.saveProgress(
            WatchProgress(
                episodeId = ctx.episodeId,
                animeId = ctx.animeId,
                animeTitle = ctx.animeTitle,
                episodeTitle = ctx.episodeTitle,
                episodeNumber = ctx.episodeNumber,
                episodeImage = ctx.episodeImage,
                animeImage = ctx.animeImage,
                positionMs = positionMs,
                durationMs = durationMs,
                progressPercent = progress.coerceIn(0f, 1f),
                isWatched = isWatched,
                updatedAt = System.currentTimeMillis()
            )
        )
        lastSavedPositionMs = positionMs
    }

    fun shouldMarkAsWatched(positionMs: Long, durationMs: Long): Boolean {
        if (durationMs <= 0) return false
        val remainingMs = durationMs - positionMs
        val progress = positionMs.toFloat() / durationMs.toFloat()
        val isShortEpisode = durationMs < 5 * 60 * 1000 // < 5 minutos

        if (isShortEpisode) {
            return remainingMs < 5_000
        }

        return remainingMs <= 90_000 || progress > 0.95f
    }

    fun shouldShowNextEpisodeOverlay(positionMs: Long, durationMs: Long): Boolean {
        if (durationMs <= 0) return false
        if (durationMs < 5 * 60 * 1000) return false // No para episodios cortos
        val remainingMs = durationMs - positionMs
        return remainingMs in 1..90_000
    }

    suspend fun markAsWatched(episodeId: Int) = repository.markAsWatched(episodeId)

    suspend fun markAsUnwatched(episodeId: Int) = repository.markAsUnwatched(episodeId)

    fun clear() {
        currentContext = null
        lastSavedPositionMs = -1L
    }
}