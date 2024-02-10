package com.premelc.tresetacounter.navigation

sealed class NavRoutes(val route: String) {
    data object MainMenu : NavRoutes("mainMenu")
    data object TresetaGame : NavRoutes("tresetaGame")
    data object BriscolaGame : NavRoutes("briscolaGame")
    data object TresetaGameCalculator : NavRoutes("tresetaGameCalculator")
    data object BriscolaGameCalculator : NavRoutes("BriscolaGameCalculator")
    data object TresetaGameHistory : NavRoutes("tresetaGameHistory")
    data object TresetaRoundEdit : NavRoutes("tresetaRoundEdit")
    data object BriscolaRoundEdit : NavRoutes("briscolaRoundEdit")
}