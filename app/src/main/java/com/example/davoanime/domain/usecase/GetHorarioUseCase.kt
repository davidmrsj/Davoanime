package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.HorarioData
import com.example.davoanime.domain.repository.AnimeJKRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHorarioUseCase @Inject constructor(
    private val repository: AnimeJKRepository
) {
    operator fun invoke(): Flow<HorarioData> {
        return repository.getHorario()
    }
}
