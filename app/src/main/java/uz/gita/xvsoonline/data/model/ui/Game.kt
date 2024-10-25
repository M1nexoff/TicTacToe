package uz.gita.xvsoonline.data.model.ui

import uz.gita.xvsoonline.data.model.firebase.FBGame
import java.io.Serializable

data class Game(
    val id: String,
    val player1: String,
    val player2: String,
    val gameData: String,
    val accept: Int = 0, //0 - waiting, 1 accepted, 2 declined
    val turn: Int = 2
):Serializable{
    fun toFBGameData() = FBGame(player1, player2, gameData, accept, turn)
}