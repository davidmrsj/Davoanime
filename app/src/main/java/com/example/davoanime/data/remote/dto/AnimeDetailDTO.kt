package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.model.RelatedAnime
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

data class AnimeDetailDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("temporada") val temporada: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("votos") val votos: Int?,
    @SerializedName("estudios") val estudios: List<String>?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("trailer") val trailer: String?,
    @SerializedName("temporadas") val temporadas: Any? = null,
    @SerializedName("relacionados") val relacionados: Any? = null
) {
    fun toDomain(): AnimeDetail {
        return AnimeDetail(
            id = id ?: 0,
            slug = slug ?: "",
            title = title ?: "",
            synopsis = synopsis ?: "",
            temporada = temporada ?: "",
            type = type ?: "",
            status = status ?: "",
            image = image ?: "",
            tipo = tipo ?: "",
            estado = estado ?: "",
            votos = votos ?: 0,
            estudios = estudios ?: emptyList(),
            tags = tags ?: emptyList(),
            trailer = trailer,
            temporadas = parseRelatedMap(temporadas),
            relacionados = parseRelatedMap(relacionados)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseRelatedMap(raw: Any?): Map<String, List<RelatedAnime>> {
        if (raw == null) return emptyMap()
        if (raw is List<*>) return emptyMap() // empty [] from API
        if (raw is LinkedTreeMap<*, *>) {
            val result = mutableMapOf<String, List<RelatedAnime>>()
            for ((key, value) in raw) {
                val category = key as? String ?: continue
                val items = (value as? List<*>)?.mapNotNull { item ->
                    if (item is LinkedTreeMap<*, *>) {
                        RelatedAnime(
                            id = (item["id"] as? Double)?.toInt() ?: 0,
                            title = item["title"] as? String ?: "",
                            slug = item["slug"] as? String ?: "",
                            image = item["image"] as? String ?: ""
                        )
                    } else null
                } ?: emptyList()
                result[category] = items
            }
            return result
        }
        return emptyMap()
    }
}
