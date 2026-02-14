package com.example.davoanime.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.model.SearchFilters
import com.example.davoanime.domain.usecase.SearchAnimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchAnimeUseCase: SearchAnimeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        debounceSearch()
    }

    fun toggleFilterSheet() {
        _state.update { it.copy(isFilterSheetVisible = !it.isFilterSheetVisible) }
    }

    fun onFilterChange(filters: SearchFilters) {
        _state.update { it.copy(filters = filters) }
    }

    fun applyFilters() {
        _state.update { it.copy(isFilterSheetVisible = false, currentPage = 1, searchResults = emptyList()) }
        performSearch()
    }

    fun clearFilters() {
        _state.update { it.copy(filters = SearchFilters(fecha = "")) }
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _state.update { it.copy(currentPage = 1, searchResults = emptyList()) }
            performSearch()
        }
    }

    private fun performSearch() {
        val currentState = _state.value
        val query = currentState.searchQuery
        val filters = currentState.filters
        val page = currentState.currentPage

        // If no query and no active filters, show empty state
        val hasActiveFilters = filters.filtro.isNotBlank() ||
                filters.tipo != "none" ||
                filters.estado != "none" ||
                filters.orden.isNotBlank() ||
                filters.fecha.isNotBlank()

        if (query.isBlank() && !hasActiveFilters) {
            _state.update {
                it.copy(
                    searchResults = emptyList(),
                    isEmpty = true,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            searchAnimeUseCase(query, filters, page)
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = page == 1,
                            isLoadingMore = page > 1,
                            error = null,
                            isEmpty = false
                        )
                    }
                }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = e.message ?: "Error searching"
                        )
                    }
                }
                .collect { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            searchResults = if (page == 1) result.items else it.searchResults + result.items,
                            currentPage = result.currentPage,
                            lastPage = result.lastPage,
                            isEmpty = page == 1 && result.items.isEmpty(),
                            error = null
                        )
                    }
                }
        }
    }

    fun loadMore() {
        val currentState = _state.value
        if (currentState.isLoadingMore || currentState.currentPage >= currentState.lastPage) return
        _state.update { it.copy(currentPage = it.currentPage + 1) }
        performSearch()
    }

    fun retry() {
        performSearch()
    }
}
