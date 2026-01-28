package com.example.davoanime.data.remote.api

import com.example.davoanime.data.remote.dto.RecentListDTO
import retrofit2.http.GET

interface AnimeJKApiService {

    @GET("recientes")
    suspend fun getExamples(): RecentListDTO

}
