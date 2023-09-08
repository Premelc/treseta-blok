package com.premelc.templateproject.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.premelc.templateproject.ui.theme.TemplateProjectTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun FeatureMainContent() {
    val viewModel: FeatureViewModel = getViewModel()
    val state = viewModel.viewState.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "some title") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, contentDescription = "")
                    }
                },
            )
        },
        backgroundColor = Color.DarkGray,
        contentColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(it)) {
            when (state) {
                is FeatureState.Available -> AvailableContent(state = state)
                FeatureState.Loading -> Loading()
                FeatureState.Unavailable -> ErrorScreen(viewModel::onRetry)
            }
        }
    }
}

@Composable
fun AvailableContent(
    state: FeatureState.Available,
) {
    LazyColumn() {
        for (quote in state.quotes) {
            item {
                Column(modifier = Modifier.padding(bottom = 10.dp, start = 5.dp, end = 5.dp)) {
                    Text(text = quote.content)
                    Text(modifier = Modifier.padding(top = 5.dp), text = quote.author)
                }
            }
        }
    }

}

@Composable
fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = Color.White,
            strokeWidth = 10.dp
        )
    }
}

@Composable
fun ErrorScreen(
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Shit happens maaan")
        Button(
            onClick = onClick
        ) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "")
            Text(text = "Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TemplateProjectTheme {
        ErrorScreen(){}
    }
}