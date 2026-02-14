package com.example.davoanime.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetAnimeDetailUseCase
import com.example.davoanime.domain.usecase.GetEpisodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: 0

    private val _state = MutableStateFlow(AnimeDetailUiState())
    val state: StateFlow<AnimeDetailUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            getAnimeDetailUseCase(animeId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading detail") }
                }
                .collect { detail ->
                    _state.update { it.copy(animeDetail = detail, isLoading = false) }
                }
        }

        viewModelScope.launch {
            getEpisodesUseCase(animeId)
                .catch { /* Silently fail for episodes preview */ }
                .collect { episodes ->
                    _state.update { it.copy(episodes = episodes) }
                }
        }
    }

    fun toggleSynopsis() {
        _state.update { it.copy(isSynopsisExpanded = !it.isSynopsisExpanded) }
    }

    fun selectTab(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }

    fun retry() {
        loadData()
    }
}
