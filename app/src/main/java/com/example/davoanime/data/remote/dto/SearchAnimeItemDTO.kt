package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.SearchAnime
import com.google.gson.annotations.SerializedName

data class SearchAnimeItemDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("estado") val estado: String?
) {
    fun toDomain(): SearchAnime {
        return SearchAnime(
            id = id ?: 0,
            title = title ?: "",
            image = image ?: "",
            slug = slug ?: "",
            url = url ?: "",
            type = type ?: "",
            status = status ?: "",
            tipo = tipo ?: "",
            estado = estado ?: ""
        )
    }
}
