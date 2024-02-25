package com.premelc.tresetacounter.uiComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.domain.mainMenu.MainMenuInteraction.TapOnLanguageItem
import com.premelc.tresetacounter.domain.mainMenu.MainMenuInteraction
import com.premelc.tresetacounter.utils.setLanguage

@Composable
internal fun LanguageDropDownMenu(
    modifier: Modifier = Modifier,
    selectedLanguage: String,
    onInteraction: (MainMenuInteraction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LanguageAccordion(
        modifier = modifier,
        selectedLanguage = selectedLanguage,
        isExpanded = expanded,
        onHeaderTap = {
            expanded = !expanded
        },
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            if (selectedLanguage != "en") {
                LanguageDropDownItem(
                    onClick = {
                        expanded = false
                        onInteraction(TapOnLanguageItem("en"))
                        context.setLanguage("en")
                    },
                    text = stringResource(R.string.language_name_english),
                    icon = painterResource(R.drawable.uk)
                )
            }
            if (selectedLanguage != "hr") {
                LanguageDropDownItem(
                    onClick = {
                        expanded = false
                        onInteraction(TapOnLanguageItem("hr"))
                        context.setLanguage("hr")
                    },
                    text = stringResource(R.string.language_name_croatian),
                    icon = painterResource(R.drawable.croatia)
                )
            }
            if (selectedLanguage != "it") {
                LanguageDropDownItem(
                    onClick = {
                        expanded = false
                        onInteraction(TapOnLanguageItem("it"))
                        context.setLanguage("it")
                    },
                    text = stringResource(R.string.language_name_italian),
                    icon = painterResource(R.drawable.italy)
                )
            }
        }
    }
}

@Composable
private fun LanguageDropDownItem(
    onClick: () -> Unit,
    text: String,
    icon: Painter,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(32.dp)
                .padding(end = 4.dp),
            painter = icon,
            contentDescription = null
        )
        Text(text)
    }
}
