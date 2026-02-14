package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.Episode
import com.google.gson.annotations.SerializedName

data class EpisodeDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("number") val number: Int?,
    @SerializedName("image") val image: String?,
    @SerializedName("animes_id") val animesId: Int?
) {
    fun toDomain(): Episode {
        return Episode(
            id = id ?: 0,
            title = title ?: "",
            number = number ?: 0,
            image = image ?: "",
            animeId = animesId ?: 0
        )
    }
}
