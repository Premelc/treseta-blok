package com.premelc.templateproject.service

import com.premelc.templateproject.data.CallsEntity
import com.premelc.templateproject.data.GameEntity
import com.premelc.templateproject.data.RoundEntity
import com.premelc.templateproject.data.SetEntity
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.domain.gameCalculator.Call
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.service.data.GameSet
import com.premelc.templateproject.service.data.GameState
import com.premelc.templateproject.service.data.toRound
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class TresetaService(private val tresetaDatabase: TresetaDatabase) {

    fun selectedGameFlow(gameId: Int) = combine(
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

    fun gamesFlow() = tresetaDatabase.gameDao().getAllGames()
    suspend fun deleteGame(gameId: Int) = tresetaDatabase.gameDao().deleteGameById(gameId)
    suspend fun insertRound(
        setId: Int,
        firstTeamPoints: Int,
        secondTeamPoints: Int,
        firstTeamCalls: List<Call>,
        secondTeamCalls: List<Call>,
    ) {
        val newTimestamp = System.currentTimeMillis()
        val roundId = tresetaDatabase.roundDao().insertRound(
            RoundEntity(
                id = 0,
                setId = setId,
                timestamp = newTimestamp,
                firstTeamPoints = firstTeamPoints,
                secondTeamPoints = secondTeamPoints,
            )
        )
        updateGameTimestamp(
            tresetaDatabase.setDao().getSingleSet(setId).first().gameId,
            newTimestamp
        )
        val callEntityList = emptyList<CallsEntity>()
        for (call in firstTeamCalls) {
            callEntityList + CallsEntity(
                id = 0,
                roundId = roundId.toInt(),
                team = Team.FIRST,
                call = call
            )
        }
        for (call in secondTeamCalls) {
            callEntityList + CallsEntity(
                id = 0,
                roundId = roundId.toInt(),
                team = Team.SECOND,
                call = call
            )
        }
        tresetaDatabase.callsDao().insertCalls(callEntityList)
    }

    suspend fun startNewGame(): Int {
        tresetaDatabase.gameDao().insertGame(
            listOf(
                GameEntity(
                    id = 0,
                    timestamp = null,
                    firstTeamPoints = 0,
                    secondTeamPoints = 0
                )
            )
        )
        val newGameId = tresetaDatabase.gameDao().getNewGame().id
        tresetaDatabase.setDao().insertSet(
            listOf(
                SetEntity(
                    id = 0,
                    gameId = newGameId
                )
            )
        )
        return newGameId
    }


    suspend fun updateCurrentGame(winningTeam: Team, game: GameState.GameReady) {
        when (winningTeam) {
            Team.FIRST -> {
                tresetaDatabase.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                tresetaDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            game.gameId,
                            System.currentTimeMillis(),
                            game.firstTeamScore + 1,
                            game.secondTeamScore,
                        )
                    )
                )
            }

            Team.SECOND -> {
                tresetaDatabase.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                tresetaDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            game.gameId,
                            System.currentTimeMillis(),
                            game.firstTeamScore,
                            game.secondTeamScore + 1,
                        )
                    )
                )
            }

            Team.NONE -> Unit
        }
    }

    private suspend fun updateGameTimestamp(gameId: Int, newTimestamp: Long) {
        tresetaDatabase.gameDao().getSingleGame(gameId).first().let {
            tresetaDatabase.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        timestamp = newTimestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints
                    )
                )
            )
        }
    }
}