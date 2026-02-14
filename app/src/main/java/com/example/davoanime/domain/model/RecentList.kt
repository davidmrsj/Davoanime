package com.example.davoanime.domain.model

data class RecentList(
    val version: String,
    val updateUrl: String,
    val recientes: List<Reciente>
)

data class Reciente(
    val id: Int,
    val number: Int,
    val title: String,
    val image: String,
    val thumbnail: String,
    val animeId: Int,
    val timestamp: String,
    val animeTitle: String = "",
    val animeType: String = "",
    val animeImage: String = "",
    val animeSlug: String = ""
)
