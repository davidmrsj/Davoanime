package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerStreamUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(episodeId: Int): Flow<String> {
        return repository.getPlayerStream(episodeId)
    }
}
