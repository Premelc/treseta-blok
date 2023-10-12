package com.premelc.templateproject.service

import androidx.compose.runtime.mutableStateOf
import com.premelc.templateproject.data.GameEntity
import com.premelc.templateproject.data.SetEntity
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.service.data.GameSet
import com.premelc.templateproject.service.data.GameState
import com.premelc.templateproject.service.data.toRound
import kotlinx.coroutines.flow.combine

class TresetaService(private val tresetaDatabase: TresetaDatabase) {

    fun selectedGameFlow(gameId: Int) =
        combine(
            tresetaDatabase.gameDao().getSingleGame(gameId),
            tresetaDatabase.setDao().getAllSets(gameId),
            tresetaDatabase.roundDao().getRounds()
        ) { game, sets, rounds ->
            GameState.GameReady(
                gameId = game.id,
                firstTeamScore = game.firstTeamPoints,
                secondTeamScore = game.secondTeamPoints,
                setList = sets.map { setEntity ->
                    GameSet(
                        id = setEntity.id,
                        roundsList = rounds.filter {
                            it.setId == setEntity.id
                        }.map { it.toRound() }
                    )
                }.sortedByDescending { it.id }
            )
        }

    suspend fun updateCurrentGame(winningTeam: Team, game: GameState.GameReady) {
        when (winningTeam) {
            Team.FIRST -> {
                tresetaDatabase.setDao().insertSet(listOf(SetEntity(id = 0, gameId = game.gameId)))
                tresetaDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            game.gameId,
                            game.firstTeamScore + 1,
                            game.secondTeamScore,
                        )
                    )
                )
            }

            Team.SECOND -> {
                tresetaDatabase.setDao().insertSet(listOf(SetEntity(id = 0, gameId = game.gameId)))
                tresetaDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            game.gameId,
                            game.firstTeamScore,
                            game.secondTeamScore + 1,
                        )
                    )
                )
            }

            Team.NONE -> Unit
        }
    }
}