package com.authorisation.mycollectsub


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalAdapter(private val goals: List<Goal>, private val itemClickListener: GoalItemClickListener) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    interface GoalItemClickListener {
        fun onGoalItemClick(category: String)
    }

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.category_text_view)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        val progressTextView: TextView = view.findViewById(R.id.progress_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.categoryTextView.text = goal.category
        holder.progressBar.max = goal.targetNumber
        holder.progressBar.progress = goal.number
        holder.progressTextView.text = "${goal.number}/${goal.targetNumber}"

        // Handle category click
        holder.itemView.setOnClickListener {
            itemClickListener.onGoalItemClick(goal.category)
        }
    }

    override fun getItemCount(): Int = goals.size
}


