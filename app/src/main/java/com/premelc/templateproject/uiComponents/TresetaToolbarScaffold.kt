package com.premelc.templateproject.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun TresetaToolbarScaffold(
    backAction: (() -> Unit)? = null,
    content: @Composable() () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        topBar = {
            backAction?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { it() },
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            content()
        }
    }
}

@Composable
internal fun TresetaToolbarScaffold(
    backAction: (() -> Unit)?,
    topBarContent: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                backAction?.let {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { it() },
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    topBarContent()
                }
            }
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            content()
        }
    }
}