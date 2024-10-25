package uz.gita.xvsoonline.ui.login

import androidx.lifecycle.LiveData
import uz.gita.xvsoonline.data.model.ui.Player

interface LoginViewModel {
    val errorMessage: LiveData<String>
    val toMenuScreen: LiveData<Player>
    var openMenuScreen: (Player)->Unit
    fun onLoginClicked(login: String,deviceId: String)
}