package uz.gita.xvsoonline.data.model.ui

import uz.gita.xvsoonline.data.model.firebase.FBPlayer
import java.io.Serializable

data class Player(
    val id: String,
    val name: String,
    val online: Boolean,
    val playing: Boolean,
    val wins: Int,
    val loses: Int
): Serializable{
    fun toFBPlayer() = FBPlayer(name, online, playing, wins, loses)
}