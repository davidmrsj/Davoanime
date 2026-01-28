package com.example.davoanime.di

import com.example.davoanime.domain.repository.AnimeJKRepository
import com.example.davoanime.domain.usecase.GetExampleItemsUseCase
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
        exampleRepository: AnimeJKRepository
    ): GetExampleItemsUseCase {
        return GetExampleItemsUseCase(exampleRepository)
    }
}
