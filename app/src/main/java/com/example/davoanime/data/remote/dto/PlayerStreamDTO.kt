package com.example.davoanime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlayerStreamDTO(
    @SerializedName("stream") val stream: String?
)
