package com.example.davoanime.data.repository

import com.example.davoanime.data.remote.api.AnimeJKApiService
import com.example.davoanime.domain.model.RecentList
import com.example.davoanime.domain.model.Reciente
import com.example.davoanime.domain.repository.AnimeJKRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AnimeJKRepositoryImpl @Inject constructor(
    private val apiService: AnimeJKApiService
) : AnimeJKRepository {

    override fun getExamples(): Flow<List<Reciente>> {
        return flow {
            emit(apiService.getExamples())
        }.map { dtoList ->
            dtoList.toDomain().recientes
        }.flowOn(Dispatchers.IO)
    }

}
