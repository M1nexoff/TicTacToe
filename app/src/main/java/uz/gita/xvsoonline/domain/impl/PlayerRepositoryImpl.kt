package uz.gita.xvsoonline.domain.impl

import android.graphics.Rect
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uz.gita.xvsoonline.app.App
import uz.gita.xvsoonline.data.model.firebase.FBPlayer
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.data.pref.Pref
import uz.gita.xvsoonline.domain.PlayerRepository
import uz.gita.xvsoonline.utils.toPlayer

class PlayerRepositoryImpl: PlayerRepository {
    companion object{
        private var instance: PlayerRepositoryImpl? = null
        fun getInstance(): PlayerRepositoryImpl{
            if (instance == null){
                instance = PlayerRepositoryImpl()
            }
            return instance!!
        }
    }
    private val firestore = Firebase.firestore
    private val localStorage = Pref(App.context)
    private val playerCollection = firestore.collection(Player::class.simpleName!!)
    private var player: Player? = null
    override fun login(name: String, deviceId: String): LiveData<Result<Player>> {
        val liveData = MutableLiveData<Result<Player>>()
        playerCollection.document(deviceId).get()
            .addOnSuccessListener {
                if (it != null && it.exists()) {
                    val player = it.toPlayer().copy(name = name,online = true, playing = false)
                    playerCollection.document(it.id)
                        .set(player.toFBPlayer())
                        .addOnSuccessListener {
                            this@PlayerRepositoryImpl.player = player
                            localStorage.playerName = player.name
                            liveData.value  = Result.success(player)
                        }
                        .addOnFailureListener {
                            liveData.value = Result.failure(it)
                        }

                } else {
                    val player = FBPlayer(name,true, playing = false, wins = 0, loses = 0)
                    playerCollection.document(deviceId).set(player)
                        .addOnSuccessListener {
                            this@PlayerRepositoryImpl.player = player.toPlayer(deviceId)
                            localStorage.playerName = player.name
                            liveData.value = Result.success(player.toPlayer(deviceId))
                        }
                        .addOnFailureListener {
                            liveData.value = Result.failure(it)
                        }
                }
            }
            .addOnFailureListener {
                liveData.value = Result.failure(it)
            }
        return liveData
    }

    override fun getAllOnline(deviceId: String): LiveData<Result<List<Player>>> {
        val liveData = MutableLiveData<Result<List<Player>>>()
        playerCollection.whereNotEqualTo(FieldPath.documentId(),deviceId)
            .whereEqualTo(FBPlayer::online.name,true)
            .whereEqualTo(FBPlayer::playing.name,false)
            .addSnapshotListener{value,error->
                if (error == null){
                    liveData.value = Result.success(value!!.map { it.toPlayer() })
                } else {
                    liveData.value = Result.failure(error)
                }
        }
        return liveData
    }

    override fun setOffline() {
        if (player == null){
            return
        }
        playerCollection.document(player!!.id).set(player!!.toFBPlayer().copy(online = false, playing = false))
    }

    override fun setOnline() {
        if (player == null){
            return
        }
        playerCollection.document(player!!.id).set(player!!.toFBPlayer().copy(online = true, playing = false))
    }

    override fun changeScore(score: Int) {
        if (score>0){
            player = player!!.copy(wins = player!!.wins + 1)
           playerCollection.document(player!!.id).set(player!!.toFBPlayer())
               .addOnSuccessListener {}.addOnFailureListener { Log.d("TTT",it.toString()) }
        }else{
            player = player!!.copy(loses = player!!.loses + 1)
           playerCollection.document(player!!.id).set(player!!.toFBPlayer())
               .addOnSuccessListener {}.addOnFailureListener { Log.d("TTT",it.toString()) }
        }
    }

    override fun getPlayer(playerId: String): LiveData<Result<Player>> {
        val liveData = MutableLiveData<Result<Player>>()
        playerCollection.document(playerId).addSnapshotListener { value, error ->
            if (error == null) {
                liveData.value = Result.success(value!!.toPlayer())
            }else{
                liveData.value = Result.failure(error)
            }
        }
        return liveData
    }

    override fun setNotPlaying() {
        if (player != null) {
            playerCollection.document(player!!.id)
                .set(player!!.toFBPlayer().copy(playing = false))
        }
    }

    override fun setPLaying() {
        if (player != null) {
            playerCollection.document(player!!.id)
                .set(player!!.toFBPlayer().copy(playing = true))
        }
    }

    // Repository function returning LiveData instead of Flow
    override fun checkLogin(): Flow<Result<Player>> = flow {
        if (localStorage.playerName.isNotEmpty()) {
            val deviceId = Settings.Secure.getString(App.context.contentResolver, Settings.Secure.ANDROID_ID)
            try {
                val snapshot = playerCollection.document(deviceId).get().await()
                if (snapshot.exists()) {
                    player = snapshot.toPlayer()
                    setOnline()
                    emit(Result.success(player!!))
                } else {
                    emit(Result.failure(Exception("Player not found")))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Exception("Player name is empty")))
        }
    }
}

