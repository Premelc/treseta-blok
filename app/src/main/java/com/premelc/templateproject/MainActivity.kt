package com.premelc.templateproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorScreen
import com.premelc.templateproject.domain.mainMenu.MainMenuScreen
import com.premelc.templateproject.domain.tresetaGame.TresetaGameScreen
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.ui.theme.TresetaBlokTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TresetaBlokTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.MainMenu.route,
                    ) {
                        composable(NavRoutes.MainMenu.route) {
                            MainMenuScreen(navController = navController)
                        }
                        composable(
                            route = NavRoutes.TresetaGame.route.plus("/{gameId}"),
                            arguments = listOf(
                                navArgument("gameId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            TresetaGameScreen(navController, it.arguments?.getInt("gameId") ?: 0)
                        }
                        composable(
                            route = NavRoutes.GameCalculator.route.plus("/{gameId}"),
                            arguments = listOf(
                                navArgument("gameId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            GameCalculatorScreen(navController, it.arguments?.getInt("gameId") ?: 0)
                        }
                    }
                }
            }
        }
    }
}
