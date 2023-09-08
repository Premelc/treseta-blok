package com.premelc.templateproject.uiComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.premelc.templateproject.R

@Composable
internal fun TresetaToolbarScaffold(
    backAction: ()-> Unit,
    title: @Composable() () -> Unit = {Text(text = "treseta")},
    content: @Composable() () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = backAction) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        backgroundColor = Color.DarkGray,
        contentColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(it)) {
            content()
        }
    }
}