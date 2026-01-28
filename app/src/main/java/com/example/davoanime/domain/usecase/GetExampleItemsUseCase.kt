package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.RecentList
import com.example.davoanime.domain.model.Reciente
import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExampleItemsUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(): Flow<List<Reciente>> {
        return repository.getExamples()
    }
}
