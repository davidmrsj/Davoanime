package com.example.davoanime.domain.repository

import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.model.HorarioData
import com.example.davoanime.domain.model.Reciente
import com.example.davoanime.domain.model.SearchFilters
import com.example.davoanime.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface AnimeJKRepository {
    fun getRecientes(): Flow<List<Reciente>>
    fun searchAnime(query: String, filters: SearchFilters, page: Int): Flow<SearchResult>
    fun getHorario(): Flow<HorarioData>
    fun getAnimeDetail(animeId: Int): Flow<AnimeDetail>
    fun getAnimeEpisodes(animeId: Int): Flow<List<Episode>>
    fun getPlayerStream(episodeId: Int): Flow<String>
}
