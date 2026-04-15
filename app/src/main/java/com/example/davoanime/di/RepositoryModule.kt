package com.example.davoanime.di

import com.example.davoanime.data.repository.AnimeJKRepositoryImpl
import com.example.davoanime.data.repository.WatchProgressRepositoryImpl
import com.example.davoanime.domain.repository.AnimeJKRepository
import com.example.davoanime.domain.repository.WatchProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAnimeJKRepository(
        repositoryImpl: AnimeJKRepositoryImpl
    ): AnimeJKRepository

    @Binds
    @Singleton
    abstract fun bindWatchProgressRepository(
        repositoryImpl: WatchProgressRepositoryImpl
    ): WatchProgressRepository
}
