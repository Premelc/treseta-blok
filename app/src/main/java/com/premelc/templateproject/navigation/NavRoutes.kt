package com.premelc.templateproject.navigation

sealed class NavRoutes(val route: String) {
    object MainMenu: NavRoutes("mainMenu")
    object TresetaGame: NavRoutes("tresetaGame")
    object GameCalculator: NavRoutes("gameCalculator")
}