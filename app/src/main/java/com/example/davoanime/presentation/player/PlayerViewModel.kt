package com.example.davoanime.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.davoanime.presentation.util.WatchProgressManager
import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.repository.WatchProgressRepository
import com.example.davoanime.domain.usecase.GetAnimeDetailUseCase
import com.example.davoanime.domain.usecase.GetEpisodesUseCase
import com.example.davoanime.domain.usecase.GetPlayerStreamUseCase
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
class PlayerViewModel @Inject constructor(
    private val getPlayerStreamUseCase: GetPlayerStreamUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    private val watchProgressManager: WatchProgressManager,
    private val watchProgressRepository: WatchProgressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialEpisodeId: Int = savedStateHandle.get<Int>("episodeId") ?: 0
    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: 0

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    private var allEpisodes: List<Episode> = emptyList()
    private var currentEpisodeId: Int = initialEpisodeId
    private var countdownJob: Job? = null
    private var overlayTriggered = false
    private var animeTitle: String = ""
    private var animeImage: String = ""

    init {
        loadAnimeDetail()
        loadEpisodes()
        loadEpisode(initialEpisodeId)
    }

    private fun loadAnimeDetail() {
        viewModelScope.launch {
            getAnimeDetailUseCase(animeId)
                .catch { /* Silently fail */ }
                .collect { detail ->
                    animeTitle = detail.title
                    animeImage = detail.image
                    // If episodes already loaded, set context now
                    setEpisodeContextIfReady()
                }
        }
    }

    private fun loadEpisodes() {
        viewModelScope.launch {
            getEpisodesUseCase(animeId)
                .catch { /* Silently fail - episodes list is optional for auto-advance */ }
                .collect { episodes ->
                    allEpisodes = episodes
                    updateLastEpisodeState()
                    setEpisodeContextIfReady()
                }
        }
    }

    private fun setEpisodeContextIfReady() {
        if (allEpisodes.isEmpty()) return
        val episode = allEpisodes.find { it.id == currentEpisodeId } ?: return
        watchProgressManager.setEpisodeContext(
            WatchProgressManager.EpisodeContext(
                episodeId = currentEpisodeId,
                animeId = animeId,
                animeTitle = animeTitle.ifEmpty { "Anime #$animeId" },
                episodeTitle = episode.title,
                episodeNumber = episode.number,
                episodeImage = episode.image,
                animeImage = animeImage
            )
        )
        _state.update { it.copy(currentEpisodeNumber = episode.number) }
    }

    private fun loadEpisode(episodeId: Int) {
        currentEpisodeId = episodeId
        overlayTriggered = false
        countdownJob?.cancel()
        setEpisodeContextIfReady()

        viewModelScope.launch {
            // Verificar si hay progreso guardado
            val savedProgress = watchProgressRepository.getEpisodeProgressOnce(episodeId)
            val savedPos = if (savedProgress != null && savedProgress.positionMs > 30_000 && !savedProgress.isWatched) {
                savedProgress.positionMs
            } else {
                0L
            }

            _state.update {
                it.copy(
                    savedPositionMs = savedPos,
                    showResumeDialog = savedPos > 0,
                    currentEpisodeId = episodeId,
                    currentAnimeId = animeId,
                    showNextEpisodeOverlay = false,
                    seriesCompleted = false
                )
            }

            // Cargar stream
            getPlayerStreamUseCase(episodeId)
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar stream") }
                }
                .collect { url ->
                    _state.update { it.copy(isLoading = false, streamUrl = url, episodeTitle = getEpisodeTitle(episodeId)) }
                }
        }
    }

    fun onPositionUpdate(positionMs: Long, durationMs: Long) {
        viewModelScope.launch {
            watchProgressManager.onPositionUpdate(positionMs, durationMs)
        }

        // Verificar auto-avance
        if (!overlayTriggered && watchProgressManager.shouldShowNextEpisodeOverlay(positionMs, durationMs)) {
            val nextEp = findNextEpisode()
            if (nextEp != null) {
                overlayTriggered = true
                startNextEpisodeCountdown(nextEp)
            } else if (!_state.value.isLastEpisode) {
                updateLastEpisodeState()
            }
        }
    }

    fun saveProgressOnExit(positionMs: Long, durationMs: Long) {
        viewModelScope.launch {
            if (positionMs > 30_000 && durationMs > 0) {
                watchProgressManager.onPositionUpdate(positionMs, durationMs)
            }
            watchProgressManager.clear()
        }
    }

    private fun startNextEpisodeCountdown(nextEpisode: Episode) {
        countdownJob?.cancel()
        _state.update {
            it.copy(
                showNextEpisodeOverlay = true,
                nextEpisodeCountdown = 5,
                nextEpisodeTitle = "Ep ${nextEpisode.number} - ${nextEpisode.title}"
            )
        }
        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _state.update { it.copy(nextEpisodeCountdown = i) }
                delay(1000)
            }
            skipToNextEpisode()
        }
    }

    fun skipToNextEpisode() {
        countdownJob?.cancel()
        _state.update { it.copy(showNextEpisodeOverlay = false) }

        val nextEp = findNextEpisode()
        if (nextEp != null) {
            viewModelScope.launch {
                watchProgressManager.markAsWatched(currentEpisodeId)
            }
            loadEpisode(nextEp.id)
        } else {
            // Serie completada
            viewModelScope.launch {
                watchProgressManager.markAsWatched(currentEpisodeId)
            }
            _state.update { it.copy(seriesCompleted = true, showNextEpisodeOverlay = false) }
        }
    }

    fun cancelAutoAdvance() {
        countdownJob?.cancel()
        _state.update { it.copy(showNextEpisodeOverlay = false) }
    }

    fun dismissResumeDialog() {
        _state.update { it.copy(showResumeDialog = false) }
    }

    fun resumeFromSaved() {
        _state.update { it.copy(showResumeDialog = false) }
        // savedPositionMs ya esta en el state, el PlayerScreen hara el seek
    }

    fun restartEpisode() {
        _state.update { it.copy(showResumeDialog = false, savedPositionMs = 0L) }
    }

    private fun findNextEpisode(): Episode? {
        val currentEp = allEpisodes.find { it.id == currentEpisodeId } ?: return null
        val currentIndex = allEpisodes.indexOf(currentEp)
        return if (currentIndex >= 0 && currentIndex < allEpisodes.size - 1) {
            allEpisodes[currentIndex + 1]
        } else {
            null
        }
    }

    private fun getEpisodeTitle(episodeId: Int): String {
        return allEpisodes.find { it.id == episodeId }?.let {
            "Ep ${it.number} - ${it.title}"
        } ?: _state.value.episodeTitle
    }

    private fun updateLastEpisodeState() {
        val currentEp = allEpisodes.find { it.id == currentEpisodeId }
        val isLast = if (currentEp != null) {
            allEpisodes.indexOf(currentEp) == allEpisodes.size - 1
        } else {
            false
        }
        _state.update { it.copy(isLastEpisode = isLast) }
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
        loadEpisode(currentEpisodeId)
    }

    override fun onCleared() {
        super.onCleared()
        watchProgressManager.clear()
    }
}
