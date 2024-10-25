package uz.gita.xvsoonline.data.model.firebase

import uz.gita.xvsoonline.data.model.ui.Player

data class FBPlayer (
    val name: String,
    val online: Boolean,
    val playing: Boolean,
    val wins: Int,
    val loses: Int

){
    fun toPlayer(id: String) = Player(id, name, online, playing, wins, loses)
}