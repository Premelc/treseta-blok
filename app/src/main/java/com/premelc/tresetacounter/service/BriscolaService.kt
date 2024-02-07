package com.premelc.tresetacounter.service

import com.premelc.tresetacounter.data.CallsEntity
import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.data.RoundEntity
import com.premelc.tresetacounter.data.SetEntity
import com.premelc.tresetacounter.data.CardGameDatabase
import com.premelc.tresetacounter.service.data.GameSet
import com.premelc.tresetacounter.service.data.GameState
import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.GameType
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class BriscolaService(private val database: CardGameDatabase) {

    private val selectedGameId = MutableStateFlow<Int?>(null)

    fun selectedGameFlow() =
        selectedGameId.flatMapLatest {
            selectedOrFirstGameFlow(it)
        }

    fun gamesFlow() = database.gameDao().getAllGames(GameType.BRISCOLA)

    fun setSelectedGame(gameId: Int) {
        selectedGameId.value = gameId
    }

    private fun selectedOrFirstGameFlow(gameId: Int?) =
        if (gameId != null) {
            combine(
                database.gameDao().getSingleGame(gameId),
                database.setDao().getAllSets(gameId),
                database.roundDao().getRounds(),
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
        database.gameDao().getLatestGame().flatMapLatest { game ->
            if (game == null) {
                flowOf(GameState.NoActiveGames)
            } else {
                combine(
                    database.setDao().getAllSets(game.id),
                    database.roundDao().getRounds(),
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
        database.gameDao().deleteGameById(gameId)
        database.setDao().getAllSets(gameId).collect {
            it.map { set ->
                database.roundDao().deleteRounds(set.id)
            }
        }
        database.setDao().deleteSets(gameId)
    }

    suspend fun toggleGameFavoriteState(gameId: Int) {
        database.gameDao().getSingleGame(gameId).first().let {
            database.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        isFavorite = !it.isFavorite,
                        timestamp = it.timestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints,
                        gameType = GameType.BRISCOLA,
                    )
                )
            )
        }
    }

    suspend fun getSingleRound(roundId: Int) =
        database.roundDao().getSingleRound(roundId).toRound()

    suspend fun deleteSingleRound(roundId: Int) {
        database.callsDao().deleteCallsFromRound(roundId)
        database.roundDao().deleteSingleRound(roundId)
    }

    suspend fun editRound(
        roundId: Int,
        setId: Int,
        firstTeamPoints: Int,
        firstTeamPointsNoCalls: Int,
        secondTeamPoints: Int,
        secondTeamPointsNoCalls: Int,
        timestamp: Long,
        firstTeamCalls: List<Call>,
        secondTeamCalls: List<Call>,
    ) {
        database.roundDao().insertRound(
            RoundEntity(
                id = roundId,
                setId = setId,
                timestamp = timestamp,
                firstTeamPoints = firstTeamPoints,
                firstTeamPointsNoCalls = firstTeamPointsNoCalls,
                secondTeamPoints = secondTeamPoints,
                secondTeamPointsNoCalls = secondTeamPointsNoCalls,
            )
        )
        database.callsDao().deleteCallsFromRound(roundId)
        database.callsDao().insertCalls(firstTeamCalls.map {
            CallsEntity(
                id = 0,
                roundId = roundId,
                team = Team.FIRST,
                call = it
            )
        } + (secondTeamCalls.map {
            CallsEntity(
                id = 0,
                roundId = roundId,
                team = Team.SECOND,
                call = it
            )
        }))
    }

    suspend fun insertRound(
        setId: Int,
        firstTeamPoints: Int,
        firstTeamPointsNoCalls: Int,
        secondTeamPoints: Int,
        secondTeamPointsNoCalls: Int,
        firstTeamCalls: List<Call>,
        secondTeamCalls: List<Call>,
    ) {
        val newTimestamp = System.currentTimeMillis()
        val roundId = database.roundDao().insertRound(
            RoundEntity(
                id = 0,
                setId = setId,
                timestamp = newTimestamp,
                firstTeamPoints = firstTeamPoints,
                firstTeamPointsNoCalls = firstTeamPointsNoCalls,
                secondTeamPoints = secondTeamPoints,
                secondTeamPointsNoCalls = secondTeamPointsNoCalls,
            )
        )
        updateGameTimestamp(
            database.setDao().getSingleSet(setId).first().gameId,
            newTimestamp
        )
        database.callsDao().insertCalls(firstTeamCalls.map {
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
        database.gameDao().insertGame(
            listOf(
                GameEntity(
                    id = 0,
                    isFavorite = false,
                    timestamp = null,
                    firstTeamPoints = 0,
                    secondTeamPoints = 0,
                    gameType = GameType.BRISCOLA,
                )
            )
        )
        val newGameId = database.gameDao().getNewGame().id
        database.setDao().insertSet(
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
                database.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                database.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            id = game.gameId,
                            isFavorite = false,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore + 1,
                            secondTeamPoints = game.secondTeamScore,
                            gameType = GameType.BRISCOLA,
                        )
                    )
                )
            }

            Team.SECOND -> {
                database.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                database.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            id = game.gameId,
                            isFavorite = game.isFavorite,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore,
                            secondTeamPoints = game.secondTeamScore + 1,
                            gameType = GameType.BRISCOLA,
                        )
                    )
                )
            }

            Team.NONE -> Unit
        }
    }

    private suspend fun updateGameTimestamp(gameId: Int, newTimestamp: Long) {
        database.gameDao().getSingleGame(gameId).first().let {
            database.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        isFavorite = it.isFavorite,
                        timestamp = newTimestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints,
                        gameType = GameType.BRISCOLA,
                    )
                )
            )
        }
    }

    private suspend fun RoundEntity.toRound(): Round {
        val calls = database.callsDao().getCallsInRound(this.id)
        return Round(
            id = this.id,
            setId = this.setId,
            timestamp = this.timestamp,
            firstTeamPoints = this.firstTeamPoints,
            firstTeamPointsNoCalls = this.firstTeamPointsNoCalls,
            secondTeamPoints = this.secondTeamPoints,
            secondTeamPointsNoCalls = this.secondTeamPointsNoCalls,
            firstTeamCalls = calls.filter { it.team == Team.FIRST }.map { it.call },
            secondTeamCalls = calls.filter { it.team == Team.SECOND }.map { it.call },
        )
    }
}