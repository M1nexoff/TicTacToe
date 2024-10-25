package uz.gita.xvsoonline.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import uz.gita.xvsoonline.R
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.databinding.ScreenLoginBinding

class LoginScreen: Fragment(R.layout.screen_login){
    private val binding by viewBinding(ScreenLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels<LoginViewModelImpl>()
    private val navController by lazy { findNavController() }

    private val errorMessageObserve = Observer<String>{
        when(it){
            "Name length must be in 3..15" -> binding.loginEditText.error = it
            "Name must not contain a space" -> binding.loginEditText.error = it
            else->{
                binding.loginEditText.error = null
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private val toMainScreenObserve = Observer<Player>{
        navController.navigate(LoginScreenDirections.actionLoginScreenToMenuScreen(it))
    }

    @SuppressLint("FragmentLiveDataObserve", "HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        viewModel.openMenuScreen = {
            navController.navigate(LoginScreenDirections.actionLoginScreenToMenuScreen(it))
        }
        viewModel.errorMessage.observe(this,errorMessageObserve)
        viewModel.toMenuScreen.observe(this,toMainScreenObserve)
        binding.loginBtn.setOnClickListener {
            viewModel.onLoginClicked(login = binding.loginEditText.text.toString(),deviceId)
        }
    }
}