package com.example.davoanime.presentation.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetWatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val getWatchHistoryUseCase: GetWatchHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialUiState())
    val state: StateFlow<HistorialUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getWatchHistoryUseCase()
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { history ->
                    _state.update { it.copy(history = history, isLoading = false) }
                }
        }
    }
}
