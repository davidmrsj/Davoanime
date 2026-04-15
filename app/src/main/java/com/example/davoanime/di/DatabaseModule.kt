package com.example.davoanime.di

import android.content.Context
import androidx.room.Room
import com.example.davoanime.data.local.AppDatabase
import com.example.davoanime.data.local.dao.WatchProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object   DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "davoanime_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWatchProgressDao(database: AppDatabase): WatchProgressDao {
        return database.watchProgressDao()
    }
}
