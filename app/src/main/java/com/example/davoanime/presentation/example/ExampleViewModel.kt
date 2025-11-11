package com.example.davoanime.presentation.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.domain.usecase.GetExampleItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val getExampleItemsUseCase: GetExampleItemsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ExampleUiState())
    val state: StateFlow<ExampleUiState> = _state.asStateFlow()

    init {
        loadExamples()
    }

    private fun loadExamples() {
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
                    _state.value = ExampleUiState(
                        isLoading = false,
                        items = items,
                        error = null
                    )
                }
        }
    }
}
