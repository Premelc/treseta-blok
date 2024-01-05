package com.premelc.templateproject.domain.tresetaGameCounter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.premelc.templateproject.R
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.ui.theme.Shapes
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.TresetaToolbarScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun TresetaGameScreen(navController: NavController) {
    val viewModel: TresetaGameViewModel = koinViewModel { parametersOf(navController) }
    TresetaToolbarScaffold(
        title = { Text(text = "treseta igra") },
    ) {
        TresetaGameContent(navController = navController)
    }
}

@Composable
internal fun TresetaGameContent(navController: NavController) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            PointListColumn(title = "MI")
            PointListColumn(title = "VI")
        }
        Row(horizontalArrangement = Arrangement.End) {
            Button(onClick = { navController.navigate(NavRoutes.GameCalculator.route) },
                shape = CircleShape) {
                Text(text = "+")
            }
        }
    }
}

@Composable
internal fun RowScope.PointListColumn(
    title: String,
    modifier: Modifier = Modifier,
    pointList: List<Int> = pointListMock,
) {
    Column(modifier = modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = Typography.h6)
        Divider(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .fillMaxWidth(),
            color = Color.Black
        )
        LazyColumn {
            items(pointList) {
                Text(
                    text = it.toString(),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = Typography.body1
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .fillMaxWidth(),
            color = Color.Black
        )
        Text(text = pointList.sum().toString(), style = Typography.h6)
    }
}

private val pointListMock = listOf(
    1, 2, 3, 4, 5, 6,
    1, 2, 3, 4, 5, 6,
)