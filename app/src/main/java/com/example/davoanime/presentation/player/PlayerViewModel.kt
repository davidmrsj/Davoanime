package com.example.davoanime.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetPlayerStreamUseCase
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
class PlayerViewModel @Inject constructor(
    private val getPlayerStreamUseCase: GetPlayerStreamUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val episodeId: Int = savedStateHandle.get<Int>("episodeId") ?: 0

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    init {
        loadStream()
    }

    private fun loadStream() {
        viewModelScope.launch {
            getPlayerStreamUseCase(episodeId)
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading stream") }
                }
                .collect { url ->
                    _state.update { it.copy(isLoading = false, streamUrl = url) }
                }
        }
    }

    fun togglePlayPause() {
        _state.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun toggleControls() {
        _state.update { it.copy(showControls = !it.showControls) }
    }

    fun hideControls() {
        _state.update { it.copy(showControls = false) }
    }

    fun showControls() {
        _state.update { it.copy(showControls = true) }
    }

    fun retry() {
        loadStream()
    }
}
