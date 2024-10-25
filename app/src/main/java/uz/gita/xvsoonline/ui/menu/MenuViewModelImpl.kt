package uz.gita.xvsoonline.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.domain.GameRepository
import uz.gita.xvsoonline.domain.PlayerRepository
import uz.gita.xvsoonline.domain.impl.GameRepositoryImpl
import uz.gita.xvsoonline.domain.impl.PlayerRepositoryImpl

class MenuViewModelImpl: MenuViewModel,ViewModel() {
    override val playerList = MutableLiveData<List<Player>>()
    override val gameList = MutableLiveData<List<Game>>()
    override val playerData = MutableLiveData<Player>()

    override val errorMessage = MutableLiveData<String>()
    override val toGameScreen = MutableLiveData<Game>()

    private val playerRepository: PlayerRepository = PlayerRepositoryImpl.getInstance()
    private val gameRepository: GameRepository = GameRepositoryImpl.getInstance()

    override fun getList(deviceId: String) {
        playerRepository.getAllOnline(deviceId).observeForever {
            it.onSuccess {
                playerList.value = it
            }
        }
    }

    override fun acceptGame(game: Game, choose: Int) {
        gameRepository.setGame(game.copy(accept = choose))
        if (choose == 1){
            playerRepository.setPLaying()
            toGameScreen.value = game
            gameList.value = gameList.value!!.toMutableList().apply { removeIf { game.id == it.id } }
            (gameList.value as MutableList<Game>).forEach {
                gameRepository.setGame(it.copy(accept = 2))
            }
        }
        if (gameList.value == null){
            return
        }
        gameList.value = gameList.value!!.toMutableList().apply { remove(game) }
    }

    override fun getGamesList(deviceId: String) {
        gameRepository.getGames(deviceId).observeForever {
            it.onSuccess {
                var list = it.toMutableList()
                gameList.value?.forEach {
                    list.remove(it)
                }
                list = list.filter { it.accept == 0 }.toMutableList()
                if (gameList.value == null){
                    gameList.value = list
                    return@onSuccess
                }
                gameList.value = gameList.value!!.toMutableList().apply { addAll(list) }
            }
        }
    }

    override fun onFightClick(deviceId: String, player: Player) {
        gameRepository.createGame(deviceId,player.id).observeForever {
            it.onSuccess {
                gameRepository.waitGame(it.id).observeForever {
                    it.onSuccess {
                        when(it.accept){
                            0->{}
                            1->{
                                playerRepository.setPLaying()
                                toGameScreen.value = it
                            }

                            2->{
                                gameRepository.deleteGame(it.id)

                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCancelClick(deviceId: String,player: Player) {
        gameRepository.deleteGame(deviceId,player.id)
    }

    override fun getPlayer(playerId: String) {
        playerRepository.getPlayer(playerId).observeForever {
            it.onSuccess {
                playerData.value = it
            }
            it.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    override fun logOut() {
        playerRepository.setOffline()


    }

}