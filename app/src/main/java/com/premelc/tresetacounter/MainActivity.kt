package com.premelc.tresetacounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.premelc.tresetacounter.domain.briscolaGame.BriscolaGameScreen
import com.premelc.tresetacounter.domain.gameCalculator.GameCalculatorScreen
import com.premelc.tresetacounter.domain.gameHistory.GameHistoryScreen
import com.premelc.tresetacounter.domain.mainMenu.MainMenuScreen
import com.premelc.tresetacounter.domain.roundEdit.RoundEditScreen
import com.premelc.tresetacounter.domain.tresetaGame.TresetaGameScreen
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.navigation.composableWrapper
import com.premelc.tresetacounter.ui.theme.TresetaBlokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TresetaBlokTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.TresetaGame.route,
                    ) {
                        composableWrapper(route = NavRoutes.TresetaGame.route) {
                            TresetaGameScreen { route: String ->
                                navController.tresetaNavigate(route)
                            }
                        }
                        composableWrapper(route = NavRoutes.BriscolaGame.route) {
                            BriscolaGameScreen { route: String ->
                                navController.tresetaNavigate(route)
                            }
                        }
                        composableWrapper(route = NavRoutes.MainMenu.route) {
                            MainMenuScreen { route: String ->
                                navController.tresetaNavigate(route)
                            }
                        }
                        composableWrapper(
                            route = NavRoutes.GameCalculator.route.plus("/{setId}"),
                            arguments = listOf(
                                navArgument("setId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            ),
                        ) {
                            GameCalculatorScreen(navController, it.arguments?.getInt("setId") ?: 0)
                        }
                        composableWrapper(
                            route = NavRoutes.RoundEdit.route.plus("/{roundId}"),
                            arguments = listOf(
                                navArgument("roundId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            ),
                        ) {
                            RoundEditScreen(navController, it.arguments?.getInt("roundId") ?: 0)
                        }
                        composableWrapper(route = NavRoutes.GameHistory.route) {
                            GameHistoryScreen(navController)
                        }
                    }
                }
            }
        }
        MobileAds.initialize(this) {}
    }

    private fun NavController.tresetaNavigate(route: String) {
        this.navigate(route)
    }
}
