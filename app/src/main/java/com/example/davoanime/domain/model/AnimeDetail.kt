package com.example.davoanime.domain.model

data class AnimeDetail(
    val id: Int,
    val slug: String,
    val title: String,
    val synopsis: String,
    val temporada: String,
    val type: String,
    val status: String,
    val image: String,
    val tipo: String,
    val estado: String,
    val votos: Int,
    val estudios: List<String>,
    val tags: List<String>,
    val trailer: String?,
    val temporadas: Map<String, List<RelatedAnime>>,
    val relacionados: Map<String, List<RelatedAnime>>
)

data class RelatedAnime(
    val id: Int,
    val title: String,
    val slug: String,
    val image: String
)
