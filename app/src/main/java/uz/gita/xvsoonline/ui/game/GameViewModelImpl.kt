package uz.gita.xvsoonline.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.domain.GameRepository
import uz.gita.xvsoonline.domain.PlayerRepository
import uz.gita.xvsoonline.domain.impl.GameRepositoryImpl
import uz.gita.xvsoonline.domain.impl.PlayerRepositoryImpl

class GameViewModelImpl : GameViewModel, ViewModel() {
    override val gameData = MutableLiveData<Game>()
    private val gameRepository: GameRepository = GameRepositoryImpl.getInstance()
    private val playerRepository: PlayerRepository = PlayerRepositoryImpl.getInstance()

    override fun getGame(gameId: String) {
        gameRepository.waitGame(gameId).observeForever {
            it.onSuccess { game ->
                gameData.value = game
            }
        }
    }

    override fun makeMove(index: Int, c: Char) {
        gameRepository.makeMove(gameData.value!!.id, index, c)
    }

    override fun changeState(score: Int) {
        playerRepository.changeScore(score)
        playerRepository.setNotPlaying()
    }
}
