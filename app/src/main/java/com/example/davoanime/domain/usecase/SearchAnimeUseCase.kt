package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.SearchFilters
import com.example.davoanime.domain.model.SearchResult
import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAnimeUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(query: String, filters: SearchFilters, page: Int): Flow<SearchResult> {
        return repository.searchAnime(query, filters, page)
    }
}
