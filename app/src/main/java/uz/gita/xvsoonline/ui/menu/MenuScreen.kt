package uz.gita.xvsoonline.ui.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.xvsoonline.R
import uz.gita.xvsoonline.data.model.ui.Game
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.databinding.ScreenMenuBinding
import uz.gita.xvsoonline.ui.SnackbarManager
import uz.gita.xvsoonline.ui.adapter.PlayersAdaptor
import uz.gita.xvsoonline.utils.myLog

class MenuScreen: Fragment(R.layout.screen_menu) {
    private val binding by viewBinding(ScreenMenuBinding::bind)
    private val viewModel: MenuViewModel by viewModels<MenuViewModelImpl>()
    private val navController by lazy { findNavController() }
    private val args: MenuScreenArgs by navArgs()
    private val adaptor by lazy { PlayersAdaptor(
        onFightClick = {
            viewModel.onFightClick(args.player.id,it)
        },
        onCancelClick = {
            viewModel.onCancelClick(args.player.id,it)
        }
    ) }
    private val snackBarManager = SnackbarManager()
    private val errorMessageObserve = Observer<String>{
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }
    private val toGameScreenObserve = Observer<Game>{
        adaptor.chosen=""
        navController.navigate(MenuScreenDirections.actionMenuScreenToGameScreen(it,args.player.id))
    }

    private val playersListObserve = Observer<List<Player>>{
        binding.emptyText.isInvisible = it.isNotEmpty()
        binding.emptyImage.isInvisible = it.isNotEmpty()
        adaptor.submitList(it)
        it.toString().myLog()
    }

    private val gameListObserve = Observer<List<Game>>{
        it.forEach {game->
            snackBarManager.showSnackbar(binding.root,"${game.player1} want to play", acceptAction = {
                viewModel.acceptGame(
                    game,
                    1
                )
            }, ignoreAction = {
            viewModel.acceptGame(
                game,
                2
            )
        }) }
    }

    private val playerObserver = Observer<Player>{
        binding.wins.text = it.wins.toString()
        binding.loses.text = it.loses.toString()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playerListRecycle.adapter = adaptor
        binding.wins.text = args.player.wins.toString()
        binding.loses.text = args.player.loses.toString()
        viewModel.getList(args.player.id)
        viewModel.getGamesList(args.player.id)
        viewModel.getPlayer(args.player.id)
        viewModel.playerData.observe(viewLifecycleOwner,playerObserver)
        viewModel.gameList.observe(this,gameListObserve)
        viewModel.playerList.observe(viewLifecycleOwner,playersListObserve)
        viewModel.toGameScreen.observe(viewLifecycleOwner,toGameScreenObserve)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.logOut()
    }
}