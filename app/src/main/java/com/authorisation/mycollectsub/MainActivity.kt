package com.authorisation.mycollectsub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.app.Application
import com.google.firebase.FirebaseApp
data class Goal(val category: String, var number: Int)

data class CollectionItem(val itemAdded: String, val description: String, val dateOfAcquisition: String)

object DataManager {
    val goals = mutableListOf<Goal>()
    val collection = mutableListOf<CollectionItem>()

    fun addCategoryWithGoal(category: String, targetNumber: Int) {
        if (!goals.any { it.category == category }) {
            goals.add(Goal(category, targetNumber))
        }
    }

    fun addItemToCollection(itemAdded: String, description: String, dateOfAcquisition: String) {
        collection.add(CollectionItem(itemAdded, description, dateOfAcquisition))
    }

    fun getAllGoals(): List<Goal> {
        return goals.toList()
    }

    fun getAllItems(): List<CollectionItem> {
        return collection.toList()
    }
}
class MainActivity : AppCompatActivity() {

    private lateinit var categoryInput: EditText
    private lateinit var goalInput: EditText
    private lateinit var itemInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addCategoryButton: Button
    private lateinit var addGoalButton: Button
    private lateinit var addItemButton: Button
    private lateinit var viewCollectionsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  enableEdgeToEdge()

        // Initialize UI components
        goalInput = findViewById(R.id.goal_number_input)
        categoryInput = findViewById(R.id.category_input)
        itemInput = findViewById(R.id.item_input)
        descriptionInput = findViewById(R.id.item_description_input)
        dateInput = findViewById(R.id.item_date_input)
        categorySpinner = findViewById(R.id.category_spinner)
        addCategoryButton = findViewById(R.id.add_category_button)
        addGoalButton = findViewById(R.id.add_goal_button)
        addItemButton = findViewById(R.id.add_item_button)
        viewCollectionsButton = findViewById(R.id.view_collections_button)

        // Populate initial categories into the Spinner
        updateCategorySpinner()

        // Handle button click to navigate to ViewCollectionsActivity
        viewCollectionsButton.setOnClickListener {
            startActivity(Intent(this, ViewCollectionsActivity::class.java))
        }

        // Method to add category
        addCategoryButton.setOnClickListener {
            val category = categoryInput.text.toString().trim()
            if (category.isNotBlank()) {
                DataManager.addCategoryWithGoal(category, 0)
                categoryInput.text.clear()
                updateCategorySpinner()
            } else {
                // Show error message if category is empty
            }
        }

        // Method to set goals for selected category
        addGoalButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem as String
            val goalNumberInput = goalInput.text.toString().toIntOrNull() ?: 0

            if (selectedCategory.isNotBlank()) {
                val existingGoal = DataManager.goals.find { it.category == selectedCategory }
                if (existingGoal != null) {
                    existingGoal.number = goalNumberInput
                    goalInput.text.clear() // Clear goal number input
                }
            }
        }

        // Method to add item to the collection
        addItemButton.setOnClickListener {
            val item = itemInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (item.isNotBlank() && description.isNotBlank()) {
                DataManager.addItemToCollection(item, description, date)
                itemInput.text.clear()
                descriptionInput.text.clear()
                dateInput.text.clear()
            } else {
                // Show error message if item or description is empty
            }
        }

        // Apply WindowInsetsListener to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateCategorySpinner() {
        val categories = DataManager.getAllGoals().map { it.category }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
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