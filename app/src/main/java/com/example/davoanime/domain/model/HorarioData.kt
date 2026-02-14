package com.example.davoanime.domain.model

data class HorarioData(
    val dayNames: Map<String, String>,
    val animes: Map<String, List<HorarioAnime>>
)

data class HorarioAnime(
    val id: Int,
    val slug: String,
    val title: String,
    val type: String,
    val image: String,
    val status: String,
    val tipo: String,
    val estado: String,
    val ultimo: UltimoEpisode?
)

data class UltimoEpisode(
    val id: Int,
    val title: String,
    val number: Int,
    val animeId: Int
)
