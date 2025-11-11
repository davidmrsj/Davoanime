package com.example.davoanime.domain.usecase

import com.example.davoanime.domain.model.ExampleItem
import com.example.davoanime.domain.repository.ExampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExampleItemsUseCase @Inject constructor(
    private val repository: ExampleRepository
) {
    operator fun invoke(): Flow<List<ExampleItem>> {
        return repository.getExamples()
    }
}
