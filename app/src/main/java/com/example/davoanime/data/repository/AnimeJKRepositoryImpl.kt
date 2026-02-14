package com.example.davoanime.data.repository

import com.example.davoanime.data.remote.api.AnimeJKApiService
import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.model.HorarioData
import com.example.davoanime.domain.model.Reciente
import com.example.davoanime.domain.model.SearchFilters
import com.example.davoanime.domain.model.SearchResult
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

    override fun getRecientes(): Flow<List<Reciente>> {
        return flow { emit(apiService.getRecientes()) }
            .map { it.toDomain().recientes }
            .flowOn(Dispatchers.IO)
    }

    override fun searchAnime(query: String, filters: SearchFilters, page: Int): Flow<SearchResult> {
        return flow {
            emit(
                apiService.searchAnime(
                    query = query,
                    page = page,
                    filtro = filters.filtro,
                    tipo = filters.tipo,
                    estado = filters.estado,
                    orden = filters.orden,
                    fecha = filters.fecha
                )
            )
        }.map { it.toDomain() }.flowOn(Dispatchers.IO)
    }

    override fun getHorario(): Flow<HorarioData> {
        return flow { emit(apiService.getHorario()) }
            .map { it.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override fun getAnimeDetail(animeId: Int): Flow<AnimeDetail> {
        return flow { emit(apiService.getAnimeDetail(animeId)) }
            .map { it.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override fun getAnimeEpisodes(animeId: Int): Flow<List<Episode>> {
        return flow { emit(apiService.getAnimeEpisodes(animeId)) }
            .map { dtoList -> dtoList.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getPlayerStream(episodeId: Int): Flow<String> {
        return flow { emit(apiService.getPlayerStream(episodeId)) }
            .map { it.stream ?: "" }
            .flowOn(Dispatchers.IO)
    }
}
