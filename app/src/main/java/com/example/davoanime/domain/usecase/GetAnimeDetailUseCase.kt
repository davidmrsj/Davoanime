package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.AnimeDetail
import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnimeDetailUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(animeId: Int): Flow<AnimeDetail> {
        return repository.getAnimeDetail(animeId)
    }
}
