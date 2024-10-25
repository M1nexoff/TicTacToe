package uz.gita.xvsoonline.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import uz.gita.xvsoonline.data.model.ui.Player

interface PlayerRepository {
    fun login(name: String,deviceId: String): LiveData<Result<Player>>
    fun getAllOnline(deviceId: String): LiveData<Result<List<Player>>>
    fun setOffline()
    fun setOnline()
    fun changeScore(score: Int)
    fun getPlayer(playerId: String): LiveData<Result<Player>>
    fun setNotPlaying()
    fun setPLaying()
    fun checkLogin() : Flow<Result<Player>>
}