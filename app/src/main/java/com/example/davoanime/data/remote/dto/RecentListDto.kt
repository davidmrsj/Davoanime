package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.RecentList
import com.example.davoanime.domain.model.Reciente

import com.google.gson.annotations.SerializedName

data class RecentListDTO(
    @SerializedName("version")
    val version: String,
    @SerializedName("update_url")
    val updateUrl: String,
    @SerializedName("recientes")
    val recientes: List<RecienteDto>,
    @SerializedName("additionalProp1")
    val additionalProp1: Map<String, Any?>? = null
) {
    fun toDomain(): RecentList {
        return RecentList(
            version = version,
            updateUrl = updateUrl,
            recientes = recientes.map { it.toDomain() }
        )
    }
}

data class RecienteDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("animes_id")
    val animeId: Int,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("additionalProp1")
    val additionalProp1: Map<String, Any?>? = null
) {
    fun toDomain(): Reciente {
        return Reciente(
            id = id,
            number = number,
            title = title,
            image = image,
            thumbnail = thumbnail,
            animeId = animeId,
            timestamp = timestamp
        )
    }
}