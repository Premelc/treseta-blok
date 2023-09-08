package com.premelc.templateproject.domain.mainMenu

import androidx.lifecycle.ViewModel

class MainMenuViewModel() : ViewModel() {

    internal fun onInteraction(interaction: MainMenuInteraction){
        when(interaction){
            MainMenuInteraction.OnNewGameClicked -> {

            }
        }
    }
}