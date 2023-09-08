package com.premelc.templateproject.domain.mainMenu

internal sealed interface MainMenuInteraction {
   object OnNewGameClicked: MainMenuInteraction
}