package uz.gita.xvsoonline.domain

import androidx.lifecycle.LiveData
import uz.gita.xvsoonline.data.model.ui.Game

interface GameRepository {
    fun createGame(player1: String,player2: String): LiveData<Result<Game>>
    fun getGames(deviceId: String): LiveData<Result<List<Game>>>
    fun setGame(game: Game):LiveData<Result<Unit>>
    fun deleteGame(deviceId: String,player2Id: String): LiveData<Result<Unit>>
    fun deleteGame(gameId: String): LiveData<Result<Unit>>
    fun waitGame(gameId:String): LiveData<Result<Game>>
    fun makeMove(gameId: String, index: Int, c: Char)
}