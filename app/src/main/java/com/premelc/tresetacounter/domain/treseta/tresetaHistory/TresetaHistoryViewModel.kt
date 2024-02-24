package com.premelc.tresetacounter.domain.treseta.tresetaHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.service.data.TresetaGameState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class TresetaHistoryViewModel(
    tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    val viewState =
        tresetaService.selectedGameFlow().flatMapLatest {
            if (it is TresetaGameState.GameReady) {
                MutableStateFlow(
                    TresetaHistoryViewState(
                        firstTeamScore = it.firstTeamScore,
                        secondTeamScore = it.secondTeamScore,
                        sets = it.setList,
                    ),
                )
            } else {
                MutableStateFlow(TresetaHistoryViewState())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TresetaHistoryViewState(),
        )

    fun onInteraction(interaction: TresetaHistoryInteraction) {
        when (interaction) {
            TresetaHistoryInteraction.OnBackButtonClicked -> navController.popBackStack()
        }
    }
}
