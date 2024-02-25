package com.premelc.tresetacounter.service

import com.premelc.tresetacounter.data.BriscolaSetEntity
import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.data.CardGameDatabase
import com.premelc.tresetacounter.service.data.BriscolaGameSet
import com.premelc.tresetacounter.service.data.BriscolaGameState
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
                database.briscolaSetDao().getAllSets(gameId),
            ) { game, sets ->
                if (game != null) {
                    BriscolaGameState.GameReady(
                        gameId = game.id,
                        isFavorite = game.isFavorite,
                        firstTeamScore = game.firstTeamPoints,
                        secondTeamScore = game.secondTeamPoints,
                        setList = sets.map { setEntity ->
                            BriscolaGameSet(
                                id = setEntity.id,
                                firstTeamPoints = setEntity.firstTeamScore,
                                secondTeamPoints = setEntity.secondTeamScore,
                            )
                        }.sortedByDescending { it.id }
                    )
                } else {
                    BriscolaGameState.NoActiveGames
                }
            }
        } else {
            latestGameFlow()
        }

    private fun latestGameFlow() =
        database.gameDao().getLatestGame().flatMapLatest { game ->
            if (game == null) {
                flowOf(BriscolaGameState.NoActiveGames)
            } else {
                database.briscolaSetDao().getAllSets(game.id).flatMapLatest { sets ->
                    setSelectedGame(game.id)
                    flowOf(
                        BriscolaGameState.GameReady(
                            gameId = game.id,
                            isFavorite = game.isFavorite,
                            firstTeamScore = game.firstTeamPoints,
                            secondTeamScore = game.secondTeamPoints,
                            setList = sets.map { setEntity ->
                                BriscolaGameSet(
                                    id = setEntity.id,
                                    firstTeamPoints = setEntity.firstTeamScore,
                                    secondTeamPoints = setEntity.secondTeamScore,
                                )
                            }.sortedByDescending { it.id }
                        )
                    )
                }
            }
        }

    suspend fun deleteGame(gameId: Int) {
        if (selectedGameId.value == gameId) selectedGameId.value = null
        database.gameDao().deleteGameById(gameId)
        database.briscolaSetDao().deleteSets(gameId)
    }

    suspend fun toggleGameFavoriteState(gameId: Int) {
        database.gameDao().getSingleGame(gameId).first()?.let {
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

    suspend fun addPointToTeam(
        setId: Int,
        winningTeam: Team,
    ) {
        database.briscolaSetDao().getSingleSet(setId).first().let { set ->
            database.briscolaSetDao().insertSet(
                listOf(
                    BriscolaSetEntity(
                        id = set.id,
                        gameId = set.gameId,
                        firstTeamScore = if (winningTeam == Team.FIRST) {
                            set.firstTeamScore + 1
                        } else {
                            set.firstTeamScore
                        },
                        secondTeamScore = if (winningTeam == Team.SECOND) {
                            set.secondTeamScore + 1
                        } else {
                            set.secondTeamScore
                        },
                    )
                )
            )
            updateGameTimestamp(
                gameId = set.gameId,
                newTimestamp = System.currentTimeMillis()
            )
        }
    }

    suspend fun removePointFromTeam(
        setId: Int,
        subtractionTeam: Team,
    ) {
        database.briscolaSetDao().getSingleSet(setId).first().let { set ->
            database.briscolaSetDao().insertSet(
                listOf(
                    BriscolaSetEntity(
                        id = set.id,
                        gameId = set.gameId,
                        firstTeamScore = (
                            if (subtractionTeam == Team.FIRST) {
                                set.firstTeamScore - 1
                            } else {
                                set.firstTeamScore
                            }
                            ).coerceAtLeast(0),
                        secondTeamScore = (
                            if (subtractionTeam == Team.SECOND) {
                                set.secondTeamScore - 1
                            } else {
                                set.secondTeamScore
                            }
                            ).coerceAtLeast(0),
                    )
                )
            )
            updateGameTimestamp(
                gameId = set.gameId,
                newTimestamp = System.currentTimeMillis()
            )
        }
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
        database.briscolaSetDao().insertSet(
            listOf(
                BriscolaSetEntity(
                    id = 0,
                    gameId = newGameId,
                    firstTeamScore = 0,
                    secondTeamScore = 0
                )
            )
        )
        setSelectedGame(newGameId)
    }

    suspend fun updateCurrentGame(winningTeam: Team, game: BriscolaGameState.GameReady) {
        when (winningTeam) {
            Team.FIRST -> {
                database.briscolaSetDao().insertSet(
                    listOf(
                        BriscolaSetEntity(
                            id = 0,
                            gameId = game.gameId,
                            firstTeamScore = 0,
                            secondTeamScore = 0,
                        )
                    )
                )
                database.gameDao().insertGame(
                    listOf(
                        GameEntity(
                            id = game.gameId,
                            isFavorite = game.isFavorite,
                            timestamp = System.currentTimeMillis(),
                            firstTeamPoints = game.firstTeamScore + 1,
                            secondTeamPoints = game.secondTeamScore,
                            gameType = GameType.BRISCOLA,
                        )
                    )
                )
            }

            Team.SECOND -> {
                database.briscolaSetDao().insertSet(
                    listOf(
                        BriscolaSetEntity(
                            id = 0,
                            gameId = game.gameId,
                            firstTeamScore = 0,
                            secondTeamScore = 0,
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
        database.gameDao().getSingleGame(gameId).first()?.let {
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
}
