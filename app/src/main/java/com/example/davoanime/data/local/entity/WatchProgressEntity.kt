package com.example.davoanime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watch_progress",
    indices = [Index(value = ["anime_id"])]
)
data class WatchProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "episode_id") val episodeId: Int,
    @ColumnInfo(name = "anime_id") val animeId: Int,
    @ColumnInfo(name = "anime_title") val animeTitle: String,
    @ColumnInfo(name = "episode_title") val episodeTitle: String,
    @ColumnInfo(name = "episode_number") val episodeNumber: Int,
    @ColumnInfo(name = "episode_image") val episodeImage: String,
    @ColumnInfo(name = "anime_image") val animeImage: String = "",
    @ColumnInfo(name = "position_ms") val positionMs: Long,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    @ColumnInfo(name = "progress_percent") val progressPercent: Float,
    @ColumnInfo(name = "is_watched") val isWatched: Boolean = false,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
