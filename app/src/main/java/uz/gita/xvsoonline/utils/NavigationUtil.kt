package uz.gita.xvsoonline.utils

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import uz.gita.xvsoonline.data.model.firebase.FBGame
import uz.gita.xvsoonline.data.model.firebase.FBPlayer
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.data.model.ui.Player

fun String.myLog() = Log.d("TTT", this)

fun Fragment.createDialog(@LayoutRes layoutRes:Int, @StyleRes styleRes:Int, cancelable:Boolean=false): Dialog {
    val dialog= Dialog(requireContext(),styleRes)
    dialog.setContentView(layoutRes)
    dialog.show()
    dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    dialog.setCancelable(cancelable)

    return dialog
}

fun <T> runOnMainThread(callback: () -> T) {
    Handler(Looper.getMainLooper()).post {
        callback()
    }
}

fun DocumentSnapshot.toPlayer(): Player = Player(
    this.id,
    get(FBPlayer::name.name).toString(),
    getBoolean(FBPlayer::online.name)!!,
    getBoolean(FBPlayer::playing.name)!!,
    getLong(FBPlayer::wins.name)!!.toInt(),
    getLong(FBPlayer::loses.name)!!.toInt()
)
fun DocumentSnapshot.toGame(): Game = Game(
    this.id,
    get(FBGame::player1.name).toString(),
    get(FBGame::player2.name).toString(),
    get(FBGame::gameData.name).toString(),
    getLong(FBGame::accept.name)?.toInt()?:0,
    getLong(FBGame::turn.name)?.toInt()?:0
)