package com.example.davoanime.presentation.horario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetHorarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HorarioViewModel @Inject constructor(
    private val getHorarioUseCase: GetHorarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HorarioUiState())
    val state: StateFlow<HorarioUiState> = _state.asStateFlow()

    init {
        loadHorario()
    }

    private fun loadHorario() {
        viewModelScope.launch {
            getHorarioUseCase()
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error loading schedule") }
                }
                .collect { data ->
                    val today = getTodayApiDay()
                    _state.value = HorarioUiState(
                        isLoading = false,
                        schedule = data.animes,
                        dayNames = data.dayNames,
                        selectedDay = today,
                        error = null
                    )
                }
        }
    }

    fun selectDay(day: String) {
        _state.update { it.copy(selectedDay = day) }
    }

    fun retry() {
        loadHorario()
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
