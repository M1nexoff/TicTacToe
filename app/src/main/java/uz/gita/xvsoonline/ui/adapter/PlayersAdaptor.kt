package uz.gita.xvsoonline.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gita.xvsoonline.R
import uz.gita.xvsoonline.data.model.ui.Player
import uz.gita.xvsoonline.databinding.ItemPlayerBinding
import uz.gita.xvsoonline.utils.myLog

class PlayersAdaptor(
    val onFightClick: (Player)->Unit,
    val onCancelClick: (Player)->Unit
) : ListAdapter<Player, PlayersAdaptor.PlayerVH>(PlayerDiffUtil) {
    var chosen = ""

    inner class PlayerVH(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.versus.setOnClickListener {
                val position = adapterPosition
                val item = getItem(position)
                if (chosen == ""){
                    binding.versus.setImageResource(R.drawable.ic_cancel)
                    chosen = item.id
                    onFightClick.invoke(getItem(position))
                }else if(chosen == item.id){
                    binding.versus.setImageResource(R.drawable.versus)
                    chosen = ""
                    onCancelClick.invoke(getItem(position))
                }
            }
        }
        fun bind(item: Player, position: Int) {
            if (item.id == chosen){
                binding.versus.setImageResource(R.drawable.ic_cancel)
            }else{
                binding.versus.setImageResource(R.drawable.versus)
            }
            item.toString().myLog()
            binding.userName.text = item.name
            binding.wins.text = "Win: ${item.wins}"
            binding.loses.text = "Lose: ${item.loses}"
        }
    }

    object PlayerDiffUtil : DiffUtil.ItemCallback<Player>() {
        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: PlayerVH, position: Int) {
        holder.bind(getItem(position),position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerVH {
        return PlayerVH(ItemPlayerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}