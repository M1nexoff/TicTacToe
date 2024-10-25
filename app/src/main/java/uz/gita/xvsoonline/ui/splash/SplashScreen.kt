package uz.gita.xvsoonline.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.gita.xvsoonline.R

class SplashScreen: Fragment(R.layout.screen_splash){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            findNavController().navigate(SplashScreenDirections.actionSplashScreenToLoginScreen())
        },5000)
    }
}