package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.SearchResult
import com.google.gson.annotations.SerializedName

data class SearchResponseDTO(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("data") val data: List<SearchAnimeItemDTO>,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("from") val from: Int?,
    @SerializedName("to") val to: Int?,
    @SerializedName("next_page_url") val nextPageUrl: String? = null,
    @SerializedName("prev_page_url") val prevPageUrl: String? = null,
    @SerializedName("first_page_url") val firstPageUrl: String? = null,
    @SerializedName("last_page_url") val lastPageUrl: String? = null
) {
    fun toDomain(): SearchResult {
        return SearchResult(
            currentPage = currentPage,
            lastPage = lastPage,
            perPage = perPage,
            total = total,
            items = data.map { it.toDomain() }
        )
    }
}
