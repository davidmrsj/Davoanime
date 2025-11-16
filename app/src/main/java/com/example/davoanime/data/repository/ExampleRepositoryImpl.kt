package com.example.davoanime.data.repository

import com.example.davoanime.data.remote.api.ExampleApiService
import com.example.davoanime.domain.model.ExampleItem
import com.example.davoanime.domain.repository.ExampleRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ExampleRepositoryImpl @Inject constructor(
    private val apiService: ExampleApiService
) : ExampleRepository {

    override fun getExamples(): Flow<List<ExampleItem>> {
        return flow {
            emit(apiService.getExamples())
        }.map { dtoList ->
            dtoList.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

}
