package uz.gita.xvsoonline.ui.game

import androidx.lifecycle.LiveData
import uz.gita.xvsoonline.data.model.ui.Game

interface GameViewModel {
    val gameData: LiveData<Game>
    fun getGame(gameId:String)
    fun makeMove(index: Int, c: Char)
    fun changeState(score: Int)
}