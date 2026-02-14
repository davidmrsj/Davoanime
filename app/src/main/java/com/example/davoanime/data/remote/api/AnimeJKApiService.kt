package com.example.davoanime.data.remote.api

import com.example.davoanime.data.remote.dto.AnimeDetailDTO
import com.example.davoanime.data.remote.dto.EpisodeDTO
import com.example.davoanime.data.remote.dto.HorarioResponseDTO
import com.example.davoanime.data.remote.dto.PlayerStreamDTO
import com.example.davoanime.data.remote.dto.RecentListDTO
import com.example.davoanime.data.remote.dto.SearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeJKApiService {

    @GET("recientes")
    suspend fun getRecientes(): RecentListDTO

    @GET("buscar")
    suspend fun searchAnime(
        @Query("s") query: String,
        @Query("p") page: Int,
        @Query("filtro") filtro: String,
        @Query("tipo") tipo: String,
        @Query("estado") estado: String,
        @Query("orden") orden: String,
        @Query("fecha") fecha: String
    ): SearchResponseDTO

    @GET("horario")
    suspend fun getHorario(): HorarioResponseDTO

    @GET("anime/{anime_id}")
    suspend fun getAnimeDetail(@Path("anime_id") animeId: Int): AnimeDetailDTO

    @GET("anime/{anime_id}/episodios")
    suspend fun getAnimeEpisodes(@Path("anime_id") animeId: Int): List<EpisodeDTO>

    @GET("players/{episode_id}")
    suspend fun getPlayerStream(@Path("episode_id") episodeId: Int): PlayerStreamDTO
}
