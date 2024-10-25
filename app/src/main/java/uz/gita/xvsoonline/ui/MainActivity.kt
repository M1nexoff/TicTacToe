package uz.gita.xvsoonline.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.gita.xvsoonline.R
import uz.gita.xvsoonline.domain.PlayerRepository
import uz.gita.xvsoonline.domain.impl.PlayerRepositoryImpl

class MainActivity : AppCompatActivity() {
    private val playerRepository: PlayerRepository= PlayerRepositoryImpl.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        playerRepository.setOnline()
    }
    override fun onStop() {
        playerRepository.setOffline()
        super.onStop()
    }
}