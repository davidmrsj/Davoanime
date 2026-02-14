package com.example.davoanime.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Horario : Screen("horario")
    object Detail : Screen("detail/{animeId}") {
        fun createRoute(animeId: Int) = "detail/$animeId"
    }
    object Episodes : Screen("episodes/{animeId}") {
        fun createRoute(animeId: Int) = "episodes/$animeId"
    }
    object Player : Screen("player/{episodeId}") {
        fun createRoute(episodeId: Int) = "player/$episodeId"
    }
}
