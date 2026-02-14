package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.HorarioAnime
import com.example.davoanime.domain.model.HorarioData
import com.example.davoanime.domain.model.UltimoEpisode
import com.google.gson.annotations.SerializedName

data class HorarioResponseDTO(
    @SerializedName("source") val source: String?,
    @SerializedName("ttl_seconds") val ttlSeconds: Int?,
    @SerializedName("cache_expires_in_seconds") val cacheExpiresInSeconds: Int?,
    @SerializedName("data") val data: HorarioDataDTO?
) {
    fun toDomain(): HorarioData {
        val dayNames = data?.dias ?: emptyMap()
        val animes = data?.animes?.mapValues { (_, animeList) ->
            animeList.map { it.toDomain() }
        } ?: emptyMap()
        return HorarioData(dayNames = dayNames, animes = animes)
    }
}

data class HorarioDataDTO(
    @SerializedName("dias") val dias: Map<String, String>?,
    @SerializedName("animes") val animes: Map<String, List<HorarioAnimeDTO>>?
)

data class HorarioAnimeDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("ultimo") val ultimo: UltimoEpisodeDTO?
) {
    fun toDomain(): HorarioAnime {
        return HorarioAnime(
            id = id ?: 0,
            slug = slug ?: "",
            title = title ?: "",
            type = type ?: "",
            image = image ?: "",
            status = status ?: "",
            tipo = tipo ?: "",
            estado = estado ?: "",
            ultimo = ultimo?.toDomain()
        )
    }
}

data class UltimoEpisodeDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("number") val number: Int?,
    @SerializedName("animes_id") val animesId: Int?
) {
    fun toDomain(): UltimoEpisode {
        return UltimoEpisode(
            id = id ?: 0,
            title = title ?: "",
            number = number ?: 0,
            animeId = animesId ?: 0
        )
    }
}
