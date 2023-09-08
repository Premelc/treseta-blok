package com.premelc.templateproject.domain.mainMenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.premelc.templateproject.R
import com.premelc.templateproject.navigation.NavRoutes
import org.koin.androidx.compose.getViewModel

@Composable
fun MainMenuContent(navController: NavController) {
    val viewModel: MainMenuViewModel = getViewModel()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.treseta_blok)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                },
            )
        },
        backgroundColor = Color.DarkGray,
        contentColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = { navController.navigate(NavRoutes.TresetaGame.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                ) {
                    Text(text = stringResource(R.string.new_game_button))
                }
            }
        }
    }
}