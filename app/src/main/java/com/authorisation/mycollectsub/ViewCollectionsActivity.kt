package com.authorisation.mycollectsub



import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ViewCollectionsActivity : AppCompatActivity() {

    private lateinit var goalRecyclerView: RecyclerView
    private lateinit var itemRecyclerView: RecyclerView
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

        // Create the CollectionItemAdapter and set it to the itemRecyclerView
        collectionItemAdapter = CollectionItemAdapter(DataManager.getAllItems())
        itemRecyclerView.adapter = collectionItemAdapter

        // Register a ListUpdateCallback to observe changes in the DataManager.collection list
        collectionItemAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                collectionItemAdapter.notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                collectionItemAdapter.notifyItemRangeRemoved(positionStart, itemCount)
            }
        })

        // Update the GoalAdapter with the initial data
        goalRecyclerView.adapter = GoalAdapter(DataManager.getAllGoals())
    }
}

