package com.example.minhaprimeiraapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minhaprimeiraapi.R
import com.example.minhaprimeiraapi.model.Item
import com.example.minhaprimeiraapi.ui.loadUrl

class ItemAdapter(
    private val items: List<Item>,
    private val itemClickListener: (Item) -> Unit,
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
        val fullNameTextView: TextView = view.findViewById(R.id.name)
        val ageTextView: TextView = view.findViewById(R.id.age)
        val addressTextView: TextView = view.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(item)
        }
        holder.fullNameTextView.text = "${item.value.name} ${item.value.surname}"

        holder.ageTextView.text = holder.itemView.context.getString(R.string.item_age, item.value.age.toString())

        holder.addressTextView.text = item.value.address

        holder.imageView.loadUrl(item.value.imageUrl)
    }
}