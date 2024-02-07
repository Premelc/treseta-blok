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
class TresetaService(private val cardGameDatabase: CardGameDatabase) {

    private val selectedGameId = MutableStateFlow<Int?>(null)

    fun selectedGameFlow() =
        selectedGameId.flatMapLatest {
            selectedOrFirstGameFlow(it)
        }

    fun gamesFlow() = cardGameDatabase.gameDao().getAllGames(GameType.TRESETA)

    fun setSelectedGame(gameId: Int) {
        selectedGameId.value = gameId
    }

    private fun selectedOrFirstGameFlow(gameId: Int?) =
        if (gameId != null) {
            combine(
                cardGameDatabase.gameDao().getSingleGame(gameId),
                cardGameDatabase.setDao().getAllSets(gameId),
                cardGameDatabase.roundDao().getRounds(),
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
        cardGameDatabase.gameDao().getLatestGame().flatMapLatest { game ->
            if (game == null) {
                flowOf(GameState.NoActiveGames)
            } else {
                combine(
                    cardGameDatabase.setDao().getAllSets(game.id),
                    cardGameDatabase.roundDao().getRounds(),
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
        cardGameDatabase.gameDao().deleteGameById(gameId)
        cardGameDatabase.setDao().getAllSets(gameId).collect {
            it.map { set ->
                cardGameDatabase.roundDao().deleteRounds(set.id)
            }
        }
        cardGameDatabase.setDao().deleteSets(gameId)
    }

    suspend fun toggleGameFavoriteState(gameId: Int) {
        cardGameDatabase.gameDao().getSingleGame(gameId).first().let {
            cardGameDatabase.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        isFavorite = !it.isFavorite,
                        timestamp = it.timestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints,
                        gameType = GameType.TRESETA,
                    )
                )
            )
        }
    }

    suspend fun getSingleRound(roundId: Int) =
        cardGameDatabase.roundDao().getSingleRound(roundId).toRound()

    suspend fun deleteSingleRound(roundId: Int) {
        cardGameDatabase.callsDao().deleteCallsFromRound(roundId)
        cardGameDatabase.roundDao().deleteSingleRound(roundId)
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
        cardGameDatabase.roundDao().insertRound(
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
        cardGameDatabase.callsDao().deleteCallsFromRound(roundId)
        cardGameDatabase.callsDao().insertCalls(firstTeamCalls.map {
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
        val roundId = cardGameDatabase.roundDao().insertRound(
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
            cardGameDatabase.setDao().getSingleSet(setId).first().gameId,
            newTimestamp
        )
        cardGameDatabase.callsDao().insertCalls(firstTeamCalls.map {
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
        cardGameDatabase.gameDao().insertGame(
            listOf(
                GameEntity(
                    id = 0,
                    isFavorite = false,
                    timestamp = null,
                    firstTeamPoints = 0,
                    secondTeamPoints = 0,
                    gameType = GameType.TRESETA,
                )
            )
        )
        val newGameId = cardGameDatabase.gameDao().getNewGame().id
        cardGameDatabase.setDao().insertSet(
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
                cardGameDatabase.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                cardGameDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            id = game.gameId,
                            isFavorite = false,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore + 1,
                            secondTeamPoints = game.secondTeamScore,
                            gameType = GameType.TRESETA,
                        )
                    )
                )
            }

            Team.SECOND -> {
                cardGameDatabase.setDao().insertSet(
                    listOf(
                        SetEntity(
                            id = 0,
                            gameId = game.gameId
                        )
                    )
                )
                cardGameDatabase.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            id = game.gameId,
                            isFavorite = game.isFavorite,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore,
                            secondTeamPoints = game.secondTeamScore + 1,
                            gameType = GameType.TRESETA,
                        )
                    )
                )
            }

            Team.NONE -> Unit
        }
    }

    private suspend fun updateGameTimestamp(gameId: Int, newTimestamp: Long) {
        cardGameDatabase.gameDao().getSingleGame(gameId).first().let {
            cardGameDatabase.gameDao().insertGame(
                listOf(
                    GameEntity(
                        id = it.id,
                        isFavorite = it.isFavorite,
                        timestamp = newTimestamp,
                        firstTeamPoints = it.firstTeamPoints,
                        secondTeamPoints = it.secondTeamPoints,
                        gameType = it.gameType,
                    )
                )
            )
        }
    }

    private suspend fun RoundEntity.toRound(): Round {
        val calls = cardGameDatabase.callsDao().getCallsInRound(this.id)
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