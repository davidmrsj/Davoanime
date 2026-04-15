package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.WatchProgress
import com.example.davoanime.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContinueWatchingUseCase @Inject constructor(
    private val repository: WatchProgressRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<WatchProgress>> {
        return repository.getContinueWatching(limit)
    }
}
