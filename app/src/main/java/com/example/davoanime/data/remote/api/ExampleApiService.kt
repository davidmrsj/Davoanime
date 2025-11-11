package com.example.davoanime.data.remote.api

import com.example.davoanime.data.remote.dto.ExampleDto
import retrofit2.http.GET

interface ExampleApiService {
    @GET("examples")
    suspend fun getExamples(): List<ExampleDto>
}
