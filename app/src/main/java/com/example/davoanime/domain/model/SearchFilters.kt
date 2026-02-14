package com.example.davoanime.domain.model

data class SearchFilters(
    val filtro: String = "",
    val tipo: String = "none",
    val estado: String = "none",
    val orden: String = "",
    val fecha: String = ""
)
