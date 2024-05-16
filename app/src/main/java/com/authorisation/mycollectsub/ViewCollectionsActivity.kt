package com.authorisation.mycollectsub


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewCollectionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collections)

        // Update UI to display collections
        updateUI()

        // Find the back button
        val backButton: Button = findViewById(R.id.back_button)

        // Set click listener for the back button
        backButton.setOnClickListener {
            // Finish the current activity and go back to MainActivity
            finish()
        }
    }

    private fun updateUI() {
        val goalsList = DataManager.getAllGoals()
        val itemList = DataManager.getAllItems()

        // Format goals list text
        val formattedGoals = StringBuilder()
        for (goal in goalsList) {
            formattedGoals.append("Category: ${goal.category}\n")
            formattedGoals.append("Goal: ${goal.number}\n\n")
        }
        findViewById<TextView>(R.id.goal_list_text_view).text = formattedGoals.toString().trim()

        // Format item list text
        val formattedItems = StringBuilder()
        for (item in itemList) {
            formattedItems.append("Item: ${item.itemAdded}\n")
            formattedItems.append("Description: ${item.description}\n")
            formattedItems.append("Date of Acquisition: ${item.dateOfAcquisition}\n\n")
        }
        findViewById<TextView>(R.id.item_list_text_view).text = formattedItems.toString().trim()
    }
}
