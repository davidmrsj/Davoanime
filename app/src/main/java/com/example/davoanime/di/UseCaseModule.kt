package com.example.davoanime.di

import com.example.davoanime.domain.repository.AnimeJKRepository
import com.example.davoanime.domain.repository.WatchProgressRepository
import com.example.davoanime.domain.usecase.GetAnimeDetailUseCase
import com.example.davoanime.domain.usecase.GetContinueWatchingUseCase
import com.example.davoanime.domain.usecase.GetEpisodesUseCase
import com.example.davoanime.domain.usecase.GetExampleItemsUseCase
import com.example.davoanime.domain.usecase.GetHorarioUseCase
import com.example.davoanime.domain.usecase.GetPlayerStreamUseCase
import com.example.davoanime.domain.usecase.GetSeriesProgressUseCase
import com.example.davoanime.domain.usecase.GetWatchHistoryUseCase
import com.example.davoanime.domain.usecase.SearchAnimeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetExampleItemsUseCase(
        repository: AnimeJKRepository
    ): GetExampleItemsUseCase {
        return GetExampleItemsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchAnimeUseCase(
        repository: AnimeJKRepository
    ): SearchAnimeUseCase {
        return SearchAnimeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetHorarioUseCase(
        repository: AnimeJKRepository
    ): GetHorarioUseCase {
        return GetHorarioUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAnimeDetailUseCase(
        repository: AnimeJKRepository
    ): GetAnimeDetailUseCase {
        return GetAnimeDetailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetEpisodesUseCase(
        repository: AnimeJKRepository
    ): GetEpisodesUseCase {
        return GetEpisodesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPlayerStreamUseCase(
        repository: AnimeJKRepository
    ): GetPlayerStreamUseCase {
        return GetPlayerStreamUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetContinueWatchingUseCase(
        repository: WatchProgressRepository
    ): GetContinueWatchingUseCase {
        return GetContinueWatchingUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSeriesProgressUseCase(
        repository: WatchProgressRepository
    ): GetSeriesProgressUseCase {
        return GetSeriesProgressUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWatchHistoryUseCase(
        repository: WatchProgressRepository
    ): GetWatchHistoryUseCase {
        return GetWatchHistoryUseCase(repository)
    }
}
