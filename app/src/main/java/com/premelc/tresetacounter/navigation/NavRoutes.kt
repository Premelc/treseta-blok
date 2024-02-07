package com.premelc.tresetacounter.navigation

sealed class NavRoutes(val route: String) {
    data object MainMenu : NavRoutes("mainMenu")
    data object TresetaGame : NavRoutes("tresetaGame")
    data object BriscolaGame : NavRoutes("briscolaGame")
    data object GameCalculator : NavRoutes("gameCalculator")
    data object GameHistory : NavRoutes("gameHistory")
    data object RoundEdit : NavRoutes("roundEdit")
}