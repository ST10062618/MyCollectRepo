import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.authorisation.mycollectsub.Goal
import com.authorisation.mycollectsub.R

class GoalAdapter(private val goals: List<Goal>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.goal_icon)
        val category: TextView = view.findViewById(R.id.goal_category)
        val number: TextView = view.findViewById(R.id.goal_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.icon.setImageResource(R.drawable.ic_goal) // change to specific image for category
        holder.category.text = goal.category
        holder.number.text = "Goal: ${goal.number}"
    }

    override fun getItemCount() = goals.size
}
