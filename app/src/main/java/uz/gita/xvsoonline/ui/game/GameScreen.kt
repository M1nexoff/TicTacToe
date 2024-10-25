package uz.gita.xvsoonline.ui.game

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.xvsoonline.R
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.databinding.ScreenGameBinding

class GameScreen : Fragment(R.layout.screen_game) {
    private val binding by viewBinding(ScreenGameBinding::bind)
    private val viewModel: GameViewModel by viewModels<GameViewModelImpl>()
    private val navController by lazy { findNavController() }
    private val args: GameScreenArgs by navArgs()
    private var win = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.container.forEachIndexed { index, view ->
            view.setOnClickListener {
                viewModel.makeMove(index, if (args.curUserId == args.game.player1) 'o' else 'x')
            }
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getGame(args.game.id)
        viewModel.gameData.observe(viewLifecycleOwner, gameDataObserve)
    }

    private val gameDataObserve = Observer<Game> { game ->
        when (checkGameEnd(game.gameData)) {
            0 -> {} // No action needed for ongoing game
            1 -> {
                if (args.curUserId == game.player1) makeWin() else makeLose()
            }

            2 -> {
                if (args.curUserId == game.player2) makeWin() else makeLose()
            }

            3 -> {
                makeDraw()

            }
        }

        binding.turn.text = if (game.turn == 1) "Player 1 turn" else "Player 2 turn"

        binding.container.forEachIndexed { index, view ->
            (view as ImageView).setImageResource(
                when (game.gameData[index]) {
                    'o' -> R.drawable.o
                    'x' -> R.drawable.x
                    else -> android.graphics.Color.TRANSPARENT
                }
            )
        }

        if ((game.turn == 1 && game.player1 == args.curUserId) || (game.turn == 2 && game.player2 == args.curUserId)) {
            binding.container.forEachIndexed { index, view ->
                (view as ImageView).isClickable = (game.gameData[index] == '#')
            }
        } else {
            binding.container.forEach { view ->
                view.isClickable = false
            }
        }
    }

    private fun makeDraw() {
        Toast.makeText(requireContext(), "DRAW", Toast.LENGTH_SHORT).show()
        animate()
    }

    private fun makeLose() {
        viewModel.changeState(-1)
        Toast.makeText(requireContext(), "LOSE", Toast.LENGTH_SHORT).show()
        animate()
    }

    private fun makeWin() {
        viewModel.changeState(1)
        Toast.makeText(requireContext(), "WIN", Toast.LENGTH_SHORT).show()
        animate()
    }

    private fun animate() {
        binding.back.isInvisible = false
        when (win) {
            in 1..3 -> {
                for (i in 0..2) {
                    val view = binding.container.getChildAt(i+ ((win-1)*3) )
                    view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).startDelay =
                        (i * 300).toLong()
                }
            }

            in 4..6->{
                for (i in 0..2) {
                    val view = binding.container.getChildAt(3*i + win - 4)
                    view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).startDelay =
                        (i - win * 3 + 1 * 300).toLong()
                }
            }

            7->{
                for (i in 0..2) {
                    val view = binding.container.getChildAt(3 * i + i)
                    view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).startDelay =
                        (i - win * 3 + 1 * 300).toLong()
                }
            }
            8->{
                for (i in 1..3) {
                    val view = binding.container.getChildAt(3 * i - i)
                    view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).startDelay =
                        (i - win * 3 + 1 * 300).toLong()
                }
            }
        }
    }

    private fun checkGameEnd(gameData: String): Int {
        val board = Array(3) { CharArray(3) }
        for (i in 0..8) {
            board[i / 3][i % 3] = gameData[i]
        }

        for (i in 0..2) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '#') {
                win = i + 1
                return if (board[i][0] == 'x') 2 else 1
            }
        }

        for (i in 0..2) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '#') {
                win = i + 4
                return if (board[0][i] == 'x') 2 else 1
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '#') {
            win = 7
            return if (board[0][0] == 'x') 2 else 1
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '#') {
            win = 8
            return if (board[0][2] == 'x') 2 else 1
        }

        // Check for a draw (no '#' characters left)
        if (!gameData.contains('#')) {
            win = 9
            return 3
        }

        return 0
    }

    override fun onDestroy() {
        if (win == 0) {
            viewModel.changeState(-1)
        }
        super.onDestroy()
    }
}
