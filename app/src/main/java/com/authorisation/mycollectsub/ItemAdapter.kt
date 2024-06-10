package com.authorisation.mycollectsub

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionItemAdapter(private var items: List<CollectionItem>) :
    RecyclerView.Adapter<CollectionItemAdapter.CollectionItemViewHolder>() {

    class CollectionItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemDescription: TextView = view.findViewById(R.id.item_description)
        val itemDate: TextView = view.findViewById(R.id.item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_item, parent, false)
        return CollectionItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.itemAdded
        holder.itemDescription.text = item.description
        holder.itemDate.text = item.dateOfAcquisition

        // Set image directly from Bitmap
        item.image?.let {
            holder.itemImage.setImageBitmap(it)
        } ?: run {
            // Handle case where image is null
            holder.itemImage.setImageResource(R.drawable.placeholder_image) // Set a placeholder image or handle it accordingly
        }
    }

    override fun getItemCount(): Int = items.size

    // Update adapter dataset
    fun updateItems(newItems: List<CollectionItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
