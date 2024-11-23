package com.example.moviebooktracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private val itemList: MutableList<Item>,
    private val onItemClickListener: (Item, Int) -> Unit
): RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.item_list_name)
        val itemGenre: TextView = itemView.findViewById(R.id.item_list_genre)
        val itemDesc: TextView = itemView.findViewById(R.id.item_list_desc)
        val itemScore: TextView = itemView.findViewById(R.id.item_list_score)
        val itemType: TextView = itemView.findViewById(R.id.item_list_type)
        val itemIsDone: CheckBox = itemView.findViewById(R.id.item_list_is_done)
        val itemIcon: ImageView = itemView.findViewById(R.id.item_list_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.name
        holder.itemGenre.text = currentItem.genre
        holder.itemDesc.text = currentItem.desc
        holder.itemScore.text = "${currentItem.score}/10"
        holder.itemType.text = currentItem.type
        holder.itemIsDone.isChecked = currentItem.isDone
        holder.itemIcon.setImageResource(currentItem.iconRes)

        holder.itemIsDone.text = if(currentItem.isDone) "Ukończono" else "Nie ukończono"

        holder.itemView.setOnClickListener{
            onItemClickListener(currentItem, position)
        }

        holder.itemIsDone.setOnCheckedChangeListener(null)
        holder.itemIsDone.setOnCheckedChangeListener { _, isChecked ->
            itemList[position] = currentItem.copy(isDone = isChecked)

            holder.itemView.post{
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

}