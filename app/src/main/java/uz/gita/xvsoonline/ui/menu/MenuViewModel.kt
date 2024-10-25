package uz.gita.xvsoonline.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.data.model.ui.Player

interface MenuViewModel {
    val playerList: LiveData<List<Player>>
    val errorMessage: LiveData<String>
    val toGameScreen: LiveData<Game>
    val gameList: LiveData<List<Game>>
    val playerData: LiveData<Player>

    fun acceptGame(game: Game,choose:Int)
    fun getGamesList(deviceId: String)
    fun getList(deviceId: String)
    fun onFightClick(deviceId: String, player: Player)
    fun onCancelClick(deviceId: String, player: Player)
    fun getPlayer(playerId: String)
    fun logOut()
}