package com.example.davoanime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.davoanime.data.local.dao.WatchProgressDao
import com.example.davoanime.data.local.entity.WatchProgressEntity

@Database(
    entities = [WatchProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchProgressDao(): WatchProgressDao
}
