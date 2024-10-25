package uz.gita.xvsoonline.data.model.firebase

import uz.gita.xvsoonline.data.model.ui.Game

data class FBGame(
    val player1: String,
    val player2: String,
    val gameData: String,
    val accept: Int = 0,
    val turn: Int = 2
){
    fun toGameDate(id: String) = Game(id, player1, player2, gameData, accept,turn)
}