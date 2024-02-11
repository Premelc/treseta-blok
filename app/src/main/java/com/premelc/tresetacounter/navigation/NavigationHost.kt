package com.premelc.tresetacounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.premelc.tresetacounter.domain.briscola.briscolaCalculator.BriscolaGameCalculatorScreen
import com.premelc.tresetacounter.domain.briscola.briscolaGame.BriscolaGameScreen
import com.premelc.tresetacounter.domain.briscola.briscolaRoundEdit.BriscolaRoundEditScreen
import com.premelc.tresetacounter.domain.mainMenu.MainMenuScreen
import com.premelc.tresetacounter.domain.treseta.tresetaCalculator.gameCalculator.TresetaGameCalculatorScreen
import com.premelc.tresetacounter.domain.treseta.tresetaGame.TresetaGameScreen
import com.premelc.tresetacounter.domain.treseta.tresetaHistory.TresetaGameHistoryScreen
import com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit.TresetaRoundEditScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MainMenu.route,
    ) {
        composableWrapper(NavRoutes.MainMenu.route) {
            MainMenuScreen { route: String ->
                navController.navigateWrapper(route)
            }
        }
        briscolaNavigation(navController)
        tresetaNavigation(navController)
    }
}

private fun NavController.navigateWrapper(route: String) {
    if (this.currentDestination?.route != route) {
        this.navigate(route)
    }
}

private fun NavGraphBuilder.briscolaNavigation(navController: NavController) {
    composableWrapper(
        route = NavRoutes.BriscolaGameCalculator.route.plus("/{setId}"),
        arguments = listOf(
            navArgument("setId") {
                type = NavType.IntType
                defaultValue = 0
            }
        ),
    ) {
        BriscolaGameCalculatorScreen(navController, it.arguments?.getInt("setId") ?: 0)
    }
    composableWrapper(NavRoutes.BriscolaGame.route) {
        BriscolaGameScreen { route: String ->
            navController.navigateWrapper(route)
        }
    }
    composableWrapper(
        route = NavRoutes.BriscolaRoundEdit.route.plus("/{roundId}"),
        arguments = listOf(
            navArgument("roundId") {
                type = NavType.IntType
                defaultValue = 0
            }
        ),
    ) {
        BriscolaRoundEditScreen(navController, it.arguments?.getInt("roundId") ?: 0)
    }
}

private fun NavGraphBuilder.tresetaNavigation(navController: NavController) {
    composableWrapper(NavRoutes.TresetaGame.route) {
        TresetaGameScreen { route: String ->
            navController.navigateWrapper(route)
        }
    }
    composableWrapper(
        route = NavRoutes.TresetaGameCalculator.route.plus("/{setId}"),
        arguments = listOf(
            navArgument("setId") {
                type = NavType.IntType
                defaultValue = 0
            }
        ),
    ) {
        TresetaGameCalculatorScreen(navController, it.arguments?.getInt("setId") ?: 0)
    }
    composableWrapper(
        route = NavRoutes.TresetaRoundEdit.route.plus("/{roundId}"),
        arguments = listOf(
            navArgument("roundId") {
                type = NavType.IntType
                defaultValue = 0
            }
        ),
    ) {
        TresetaRoundEditScreen(navController, it.arguments?.getInt("roundId") ?: 0)
    }
    composableWrapper(route = NavRoutes.TresetaGameHistory.route) {
        TresetaGameHistoryScreen(navController)
    }
}