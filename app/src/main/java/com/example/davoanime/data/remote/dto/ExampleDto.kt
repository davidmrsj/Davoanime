package com.example.davoanime.data.remote.dto

import com.example.davoanime.domain.model.ExampleItem

data class ExampleDto(
    val id: String,
    val title: String
) {
    fun toDomain(): ExampleItem {
        return ExampleItem(
            id = id,
            title = title
        )
    }
}
