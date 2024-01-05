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
import com.premelc.templateproject.service.data.Round
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class TresetaService(private val tresetaDatabase: TresetaDatabase) {

    private val selectedGameId = MutableStateFlow<Int?>(null)

    fun selectedGameFlow() =
        selectedGameId.flatMapLatest {
            selectedOrFirstGameFlow(it)
        }

    fun gamesFlow() = tresetaDatabase.gameDao().getAllGames()

    fun setSelectedGame(gameId: Int) {
        selectedGameId.value = gameId
    }

    private fun selectedOrFirstGameFlow(gameId: Int?) =
        if (gameId != null) {
            combine(
                tresetaDatabase.gameDao().getSingleGame(gameId),
                tresetaDatabase.setDao().getAllSets(gameId),
                tresetaDatabase.roundDao().getRounds(),
            ) { game, sets, rounds ->
                GameState.GameReady(
                    gameId = game.id,
                    isFavorite = game.isFavorite,
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
        } else {
            latestGameFlow()
        }

    private fun latestGameFlow() =
        tresetaDatabase.gameDao().getLatestGame().flatMapLatest { game ->
            if (game == null) {
                flowOf(GameState.NoActiveGames)
            } else {
                combine(
                    tresetaDatabase.setDao().getAllSets(game.id),
                    tresetaDatabase.roundDao().getRounds(),
                ) { sets, rounds ->
                    setSelectedGame(game.id)
                    GameState.GameReady(
                        gameId = game.id,
                        isFavorite = game.isFavorite,
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
            }
        }

    suspend fun deleteGame(gameId: Int) {
        if (selectedGameId.value == gameId) selectedGameId.value = null
        tresetaDatabase.gameDao().deleteGameById(gameId)
        tresetaDatabase.setDao().getAllSets(gameId).collect {
            it.map { set ->
                tresetaDatabase.roundDao().deleteRounds(set.id)
            }
        }
        tresetaDatabase.setDao().deleteSets(gameId)
    }

    suspend fun toggleGameFavoriteState(gameId: Int) {
        tresetaDatabase.gameDao().getSingleGame(gameId).first().let {
            tresetaDatabase.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        isFavorite = !it.isFavorite,
                        timestamp = it.timestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints,
                    )
                )
            )
        }
    }

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
        tresetaDatabase.callsDao().insertCalls(firstTeamCalls.map {
            CallsEntity(
                id = 0,
                roundId = roundId.toInt(),
                team = Team.FIRST,
                call = it
            )
        } + (secondTeamCalls.map {
            CallsEntity(
                id = 0,
                roundId = roundId.toInt(),
                team = Team.SECOND,
                call = it
            )
        }))
    }

    suspend fun startNewGame() {
        tresetaDatabase.gameDao().insertGame(
            listOf(
                GameEntity(
                    id = 0,
                    isFavorite = false,
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
        setSelectedGame(newGameId)
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
                            id = game.gameId,
                            isFavorite = false,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore + 1,
                            secondTeamPoints = game.secondTeamScore,
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
                            id = game.gameId,
                            isFavorite = game.isFavorite,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore,
                            secondTeamPoints = game.secondTeamScore + 1,
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
                        isFavorite = it.isFavorite,
                        timestamp = newTimestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints
                    )
                )
            )
        }
    }

    private suspend fun RoundEntity.toRound(): Round {
        val calls = tresetaDatabase.callsDao().getCallsInRound(this.id)
        return Round(
            id = this.id,
            timestamp = this.timestamp,
            firstTeamPoints = this.firstTeamPoints,
            secondTeamPoints = this.secondTeamPoints,
            firstTeamCalls = calls.filter { it.team == Team.FIRST }.map { it.call },
            secondTeamCalls = calls.filter { it.team == Team.SECOND }.map { it.call },
        )
    }
}