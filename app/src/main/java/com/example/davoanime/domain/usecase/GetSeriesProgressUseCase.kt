package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeriesProgressUseCase @Inject constructor(
    private val repository: WatchProgressRepository
) {
    operator fun invoke(animeId: Int): Flow<List<WatchProgress>> {
        return repository.getSeriesProgress(animeId)
    }
}
