package com.premelc.tresetacounter.ui.theme

import androidx.compose.material.lightColors

val lightColors = with(ColorPalette) {
    lightColors(
        primary = steelBlue,
        secondary = totalBlack,
        background = lightGray,
        surface = lightGray,
        onPrimary = totalWhite,
        onSecondary = totalWhite,
        onBackground = totalBlack,
        onSurface = totalBlack,
    )
}

val darkColors = with(ColorPalette) {
    lightColors(
        primary = slateBlue,
        secondary = totalWhite,
        background = midnightBlue,
        surface = slateBlue,
        onPrimary = totalWhite,
        onSecondary = totalWhite,
        onBackground = totalWhite,
        onSurface = totalWhite,
    )
}
