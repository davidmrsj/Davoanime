package com.example.davoanime.domain.model

data class SearchResult(
    val currentPage: Int,
    val lastPage: Int,
    val perPage: Int,
    val total: Int,
    val items: List<SearchAnime>
)
