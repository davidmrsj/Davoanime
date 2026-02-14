package com.example.davoanime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.davoanime.presentation.detail.AnimeDetailScreen
import com.example.davoanime.presentation.episodes.EpisodesScreen
import com.example.davoanime.presentation.example.HomeScreen
import com.example.davoanime.presentation.horario.HorarioScreen
import com.example.davoanime.presentation.player.PlayerScreen
import com.example.davoanime.presentation.search.SearchScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(Screen.Horario.route) {
            HorarioScreen(navController = navController)
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("animeId") { type = NavType.IntType })
        ) {
            AnimeDetailScreen(navController = navController)
        }

        composable(
            route = Screen.Episodes.route,
            arguments = listOf(navArgument("animeId") { type = NavType.IntType })
        ) {
            EpisodesScreen(navController = navController)
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("episodeId") { type = NavType.IntType })
        ) {
            PlayerScreen(navController = navController)
        }
    }
}
