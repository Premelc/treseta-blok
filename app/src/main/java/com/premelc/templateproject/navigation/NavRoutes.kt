package com.premelc.templateproject.navigation

sealed class NavRoutes(val route: String) {
    data object MainMenu : NavRoutes("mainMenu")
    data object TresetaGame : NavRoutes("tresetaGame")
    data object GameCalculator : NavRoutes("gameCalculator")
    data object GameHistory : NavRoutes("gameHistory")
}