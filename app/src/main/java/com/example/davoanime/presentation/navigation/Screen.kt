package com.example.davoanime.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Horario : Screen("horario")
    object Detail : Screen("detail/{animeId}?imageUrl={imageUrl}") {
        fun createRoute(animeId: Int, imageUrl: String) = "detail/$animeId?imageUrl=${Uri.encode(imageUrl)}"
    }
    object Episodes : Screen("episodes/{animeId}") {
        fun createRoute(animeId: Int) = "episodes/$animeId"
    }
    object Player : Screen("player/{episodeId}/{animeId}") {
        fun createRoute(episodeId: Int, animeId: Int) = "player/$episodeId/$animeId"
    }
    object Historial : Screen("historial")
}
