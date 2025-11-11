package com.example.davoanime.di

import com.example.davoanime.data.repository.ExampleRepositoryImpl
import com.example.davoanime.domain.repository.ExampleRepository
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
    abstract fun bindExampleRepository(
        repositoryImpl: ExampleRepositoryImpl
    ): ExampleRepository
}
