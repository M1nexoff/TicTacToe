package uz.gita.xvsoonline.domain.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uz.gita.xvsoonline.data.model.firebase.FBGame
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.domain.GameRepository
import uz.gita.xvsoonline.utils.toGame

class GameRepositoryImpl: GameRepository{
    companion object{
        private var instance: GameRepositoryImpl? = null
        fun getInstance(): GameRepositoryImpl{
            if (instance == null){
                instance = GameRepositoryImpl()
            }
            return instance!!
        }
    }
    private val firestore = Firebase.firestore
    private val playerCollection = firestore.collection(Player::class.simpleName!!)
    private val gameCollection = firestore.collection(Game::class.simpleName!!)
    override fun createGame(player1: String, player2: String): LiveData<Result<Game>> {
        val game = FBGame(player1,player2,"#".repeat(9),0,1)
        val liveData = MutableLiveData<Result<Game>>()
        gameCollection.add(game)
            .addOnSuccessListener {
                liveData.value = Result.success(game.toGameDate(it.id))
            }
            .addOnFailureListener {
                liveData.value = Result.failure(it)
            }
        return liveData
    }

    override fun getGames(deviceId: String): LiveData<Result<List<Game>>> {
        val liveData = MutableLiveData<Result<List<Game>>>()
        gameCollection.whereEqualTo(FBGame::player2.name,deviceId)
            .addSnapshotListener{value, error->
            if (error == null){
                liveData.value = Result.success(value!!.map { it.toGame() })
            }else{
                liveData.value = Result.failure(error)
            }
        }
        return liveData
    }

    override fun setGame(game: Game): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>()
        gameCollection.document(game.id).set(game.toFBGameData())
            .addOnSuccessListener {
                liveData.value = Result.success(Unit)
            }.addOnFailureListener {
                liveData.value = Result.failure(it)
            }
        return liveData
    }

    override fun deleteGame(deviceId: String, player2Id: String): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>()
        gameCollection.whereEqualTo(Game::player1.name,deviceId).whereEqualTo(Game::player2.name,player2Id)
            .get()
            .addOnSuccessListener {
                gameCollection.document(it.first().id).delete()
                liveData.value = Result.success(Unit)
            }
            .addOnFailureListener {
                liveData.value = Result.failure(it)
            }
        return liveData
    }
    override fun deleteGame(gameId: String): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>()
        gameCollection.document(gameId).delete()
            .addOnSuccessListener {
                liveData.value = Result.success(Unit)
            }
            .addOnFailureListener {
                liveData.value = Result.failure(it)
            }
        return liveData
    }

    override fun waitGame(gameId: String): LiveData<Result<Game>> {
        val liveData = MutableLiveData<Result<Game>>()
        gameCollection.document(gameId).addSnapshotListener { value, error ->
            if (error==null){
                liveData.value = Result.success(value!!.toGame())
            }else{
                liveData.value = Result.failure(error)
            }
        }
        return liveData
    }

    override fun makeMove(gameId: String, index: Int, c: Char) {
        gameCollection.document(gameId).get().addOnSuccessListener {
            val game = it.toGame().toFBGameData()
            val gameData = StringBuilder(game.gameData)
            gameData[index] = c
            val newGame = game.copy(gameData = gameData.toString(), turn = if (c=='o')2 else 1)
            gameCollection.document(gameId).set(newGame)
        }
    }


}