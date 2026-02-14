package com.example.davoanime.domain.model

data class SearchAnime(
    val id: Int,
    val title: String,
    val image: String,
    val slug: String,
    val url: String,
    val type: String = "",
    val status: String = "",
    val tipo: String = "",
    val estado: String = ""
)
