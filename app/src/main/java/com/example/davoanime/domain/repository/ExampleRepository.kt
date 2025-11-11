package com.example.davoanime.domain.repository

import com.example.davoanime.domain.model.ExampleItem
import kotlinx.coroutines.flow.Flow

interface ExampleRepository {
    fun getExamples(): Flow<List<ExampleItem>>
}
