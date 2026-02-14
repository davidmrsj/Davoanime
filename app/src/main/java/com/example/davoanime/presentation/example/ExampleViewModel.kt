package com.example.davoanime.presentation.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetExampleItemsUseCase
import com.example.davoanime.domain.usecase.GetHorarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val getExampleItemsUseCase: GetExampleItemsUseCase,
    private val getHorarioUseCase: GetHorarioUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ExampleUiState())
    val state: StateFlow<ExampleUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getExampleItemsUseCase()
                .onStart {
                    _state.update { current ->
                        current.copy(isLoading = true, error = null)
                    }
                }
                .catch { throwable ->
                    _state.update {
                        it.copy(isLoading = false, error = throwable.message)
                    }
                }
                .collect { items ->
                    _state.update {
                        it.copy(isLoading = false, items = items, error = null)
                    }
                }
        }

        viewModelScope.launch {
            getHorarioUseCase()
                .catch { /* Silently fail for schedule section */ }
                .collect { data ->
                    val todayKey = getTodayApiDay()
                    val todayAnimes = data.animes[todayKey] ?: emptyList()
                    val todayName = data.dayNames[todayKey] ?: ""
                    _state.update {
                        it.copy(todayAnimes = todayAnimes, todayName = todayName)
                    }
                }
        }
    }

    fun retry() {
        loadData()
    }

    private fun getTodayApiDay(): String {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (today) {
            Calendar.SUNDAY -> "7"
            Calendar.MONDAY -> "1"
            Calendar.TUESDAY -> "2"
            Calendar.WEDNESDAY -> "3"
            Calendar.THURSDAY -> "4"
            Calendar.FRIDAY -> "5"
            Calendar.SATURDAY -> "6"
            else -> "1"
        }
    }
}
