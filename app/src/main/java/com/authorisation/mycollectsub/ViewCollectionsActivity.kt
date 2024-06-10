package com.authorisation.mycollectsub

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewCollectionsActivity : AppCompatActivity(), GoalAdapter.GoalItemClickListener {

    private lateinit var goalRecyclerView: RecyclerView
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var collectionItemAdapter: CollectionItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collections)

        // Initialize RecyclerViews
        goalRecyclerView = findViewById(R.id.goal_recycler_view)
        itemRecyclerView = findViewById(R.id.item_recycler_view)

        // Set Layout Managers
        goalRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set Back button click listener
        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish()
        }

        // Create and set GoalAdapter to goalRecyclerView
        goalAdapter = GoalAdapter(DataManager.getAllGoals(), this)
        goalRecyclerView.adapter = goalAdapter

        // Create and set CollectionItemAdapter to itemRecyclerView
        collectionItemAdapter = CollectionItemAdapter(emptyList()) // Initialize with empty list
        itemRecyclerView.adapter = collectionItemAdapter
    }

    // Handle category (goal) item click
    override fun onGoalItemClick(category: String) {
        // Filter items based on the selected category
        val itemsForCategory = DataManager.getAllItems().filter { it.itemAdded == category }
        collectionItemAdapter.updateItems(itemsForCategory)
    }
}
