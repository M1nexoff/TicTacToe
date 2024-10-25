package uz.gita.xvsoonline.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.domain.PlayerRepository
import uz.gita.xvsoonline.domain.impl.PlayerRepositoryImpl

class LoginViewModelImpl: LoginViewModel,ViewModel() {
    override val errorMessage = MutableLiveData<String>()
    override val toMenuScreen = MutableLiveData<Player>()
    override var openMenuScreen: (Player) -> Unit = {}
    private val playerRepository: PlayerRepository = PlayerRepositoryImpl.getInstance()
    init {
        playerRepository.checkLogin().onEach {
            it.onSuccess {
                toMenuScreen.value = it
            }
        }.launchIn(viewModelScope)
    }
    override fun onLoginClicked(login: String, deviceId: String) {
        if (login.length !in 3..15){
            errorMessage.value = "Name length must be in 3..15"
            return
        }
        if (login.contains(" ")){
            errorMessage.value = "Name must not contain a space"
            return
        }

        playerRepository.login(login,deviceId).observeForever {
            it.onSuccess {
                toMenuScreen.value = it
            }
            it.onFailure {
                errorMessage.value = it.message
            }
        }

    }

}