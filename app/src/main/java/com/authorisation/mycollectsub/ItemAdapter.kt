import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.authorisation.mycollectsub.CollectionItem
import com.authorisation.mycollectsub.R

class ItemAdapter(private val items: List<CollectionItem>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.item_icon)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val description: TextView = view.findViewById(R.id.item_description)
        val date: TextView = view.findViewById(R.id.item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(R.drawable.ic_item)  // change to specific image for item
        holder.itemName.text = item.itemAdded
        holder.description.text = item.description
        holder.date.text = item.dateOfAcquisition
    }

    override fun getItemCount() = items.size
}
