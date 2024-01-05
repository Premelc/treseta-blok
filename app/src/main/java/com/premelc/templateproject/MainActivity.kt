package com.premelc.templateproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import com.premelc.templateproject.domain.gameHistory.GameHistoryScreen
import com.premelc.templateproject.domain.mainMenu.MainMenuScreen
import com.premelc.templateproject.domain.tresetaGame.TresetaGameScreen
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.ui.theme.TresetaBlokTheme

private const val TRANSITION_DURATION = 500

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
                        composable(
                            route = NavRoutes.TresetaGame.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                        ) {
                            TresetaGameScreen(navController)
                        }
                        composable(
                            route = NavRoutes.MainMenu.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            }
                        ) {
                            MainMenuScreen(navController = navController)
                        }
                        composable(
                            route = NavRoutes.GameCalculator.route.plus("/{setId}"),
                            arguments = listOf(
                                navArgument("setId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            ),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                        ) {
                            GameCalculatorScreen(navController, it.arguments?.getInt("setId") ?: 0)
                        }
                        composable(
                            route = NavRoutes.GameHistory.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                    animationSpec = tween(TRANSITION_DURATION)
                                )
                            },
                        ) {
                            GameHistoryScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
