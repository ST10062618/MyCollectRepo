package com.authorisation.mycollectsub


import GoalAdapter
import ItemAdapter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ViewCollectionsActivity : AppCompatActivity() {

    private lateinit var goalRecyclerView: RecyclerView
    private lateinit var itemRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collections)

        // Initialize RecyclerViews
        goalRecyclerView = findViewById(R.id.goal_recycler_view)
        itemRecyclerView = findViewById(R.id.item_recycler_view)

        // Set Layout Managers
        goalRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.layoutManager = LinearLayoutManager(this)

        // Update UI with data
        updateUI()

        // Set Back button click listener
        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        val goalsList = DataManager.getAllGoals()
        val itemList = DataManager.getAllItems()

        // Set Adapters
        goalRecyclerView.adapter = GoalAdapter(goalsList)
        itemRecyclerView.adapter = ItemAdapter(itemList)
    }
}
