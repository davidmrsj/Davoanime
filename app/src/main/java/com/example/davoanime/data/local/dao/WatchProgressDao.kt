package com.example.davoanime.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.davoanime.data.local.entity.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProgressDao {

    @Query("""
        SELECT * FROM watch_progress
        WHERE is_watched = 0 AND position_ms > 0
        ORDER BY updated_at DESC
        LIMIT :limit
    """)
    fun getContinueWatching(limit: Int = 10): Flow<List<WatchProgressEntity>>

    @Query("SELECT * FROM watch_progress WHERE episode_id = :episodeId")
    fun getEpisodeProgress(episodeId: Int): Flow<WatchProgressEntity?>

    @Query("SELECT * FROM watch_progress WHERE episode_id = :episodeId")
    suspend fun getEpisodeProgressOnce(episodeId: Int): WatchProgressEntity?

    @Query("SELECT * FROM watch_progress WHERE anime_id = :animeId")
    fun getSeriesProgress(animeId: Int): Flow<List<WatchProgressEntity>>

    @Query("SELECT * FROM watch_progress ORDER BY updated_at DESC")
    fun getWatchHistory(): Flow<List<WatchProgressEntity>>

    @Upsert
    suspend fun upsertProgress(progress: WatchProgressEntity)

    @Query("UPDATE watch_progress SET is_watched = 1, updated_at = :timestamp WHERE episode_id = :episodeId")
    suspend fun markAsWatched(episodeId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE watch_progress SET is_watched = 0, position_ms = 0, progress_percent = 0.0, updated_at = :timestamp WHERE episode_id = :episodeId")
    suspend fun markAsUnwatched(episodeId: Int, timestamp: Long = System.currentTimeMillis())
}
