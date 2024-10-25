package uz.gita.xvsoonline.ui

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarManager {

    private val snackbarQueue: MutableList<SnackbarData> = mutableListOf()
    private var isShowingSnackbar: Boolean = false

    fun showSnackbar(view: View, message: String, acceptAction: View.OnClickListener, ignoreAction: View.OnClickListener) {
        snackbarQueue.add(SnackbarData(view, message, acceptAction, ignoreAction))
        if (!isShowingSnackbar) {
            showNextSnackbar()
        }
    }

    private fun showNextSnackbar() {
        if (snackbarQueue.isNotEmpty()) {
            val (view, message, acceptAction, ignoreAction) = snackbarQueue.removeAt(0)
            isShowingSnackbar = true
            try {
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).apply {
                    setAction("Accept", acceptAction)
                    addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            isShowingSnackbar = false
                            showNextSnackbar()
                        }
                    })
                }.show()
            } catch (e: Exception){
                String
                e.printStackTrace()
            }
        }
    }

    private data class SnackbarData(
        val view: View,
        val message: String,
        val acceptAction: View.OnClickListener,
        val ignoreAction: View.OnClickListener
    )
}
