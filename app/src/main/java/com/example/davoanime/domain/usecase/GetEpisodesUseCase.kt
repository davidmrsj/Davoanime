package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.Episode
import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEpisodesUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(animeId: Int): Flow<List<Episode>> {
        return repository.getAnimeEpisodes(animeId)
    }
}
