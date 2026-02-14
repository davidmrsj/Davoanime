package com.example.davoanime.presentation.search

import com.example.davoanime.domain.model.SearchAnime
import com.example.davoanime.domain.model.SearchFilters

data class SearchUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchAnime> = emptyList(),
    val filters: SearchFilters = SearchFilters(),
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val isFilterSheetVisible: Boolean = false
)
