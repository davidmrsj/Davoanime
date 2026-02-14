package com.example.davoanime.presentation.episodes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.usecase.GetAnimeDetailUseCase
import com.example.davoanime.domain.usecase.GetEpisodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: 0
    private var allEpisodes: List<Episode> = emptyList()
    private val pageSize = 20

    private val _state = MutableStateFlow(EpisodesUiState())
    val state: StateFlow<EpisodesUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getAnimeDetailUseCase(animeId)
                .catch { }
                .collect { detail ->
                    _state.update { it.copy(animeTitle = detail.title) }
                }
        }

        viewModelScope.launch {
            getEpisodesUseCase(animeId)
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading episodes") }
                }
                .collect { episodes ->
                    allEpisodes = episodes
                    val initial = episodes.take(pageSize)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            displayedEpisodes = initial,
                            allEpisodesCount = episodes.size,
                            hasMore = episodes.size > pageSize
                        )
                    }
                }
        }
    }

    fun loadMore() {
        val currentCount = _state.value.displayedEpisodes.size
        if (currentCount >= allEpisodes.size) return

        val nextBatch = allEpisodes.take(currentCount + pageSize)
        _state.update {
            it.copy(
                displayedEpisodes = nextBatch,
                hasMore = nextBatch.size < allEpisodes.size
            )
        }
    }

    fun retry() {
        loadData()
    }
}
