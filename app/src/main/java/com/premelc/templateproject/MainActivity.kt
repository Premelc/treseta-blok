package com.premelc.templateproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorScreen
import com.premelc.templateproject.domain.mainMenu.MainMenuContent
import com.premelc.templateproject.domain.tresetaGameCounter.TresetaGameScreen
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.ui.theme.TresetaBlokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
                            MainMenuContent(navController = navController)
                        }
                        composable(NavRoutes.TresetaGame.route) {
                            TresetaGameScreen(navController = navController)
                        }
                        composable(NavRoutes.GameCalculator.route) {
                            GameCalculatorScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
