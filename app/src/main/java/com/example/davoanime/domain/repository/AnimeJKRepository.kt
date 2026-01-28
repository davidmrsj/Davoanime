package com.example.davoanime.domain.repository

import com.example.davoanime.domain.model.RecentList
import com.example.davoanime.domain.model.Reciente
import kotlinx.coroutines.flow.Flow

interface AnimeJKRepository {
    fun getExamples(): Flow<List<Reciente>>
}
