package com.authorisation.mycollectsub

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

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
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        // Set up the navigation drawer
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // Populate initial categories into the Spinner
        updateCategorySpinner()

        // Handle button click to navigate to ViewCollectionsActivity
        viewCollectionsButton.setOnClickListener {
            startActivity(Intent(this, ViewCollectionsActivity::class.java))
        }

        // Method to add category
        addCategoryButton.setOnClickListener {
            try {
                val category = categoryInput.text.toString().trim()
                if (category.isNotBlank()) {
                    DataManager.addCategoryWithGoal(category, 0)
                    categoryInput.text.clear()
                    updateCategorySpinner()
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Category cannot be empty", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error adding category: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        // Method to set goals for selected category
        addGoalButton.setOnClickListener {
            try {
                val selectedCategory = categorySpinner.selectedItem as String
                val goalNumberInput = goalInput.text.toString().toIntOrNull() ?: 0

                if (selectedCategory.isNotBlank()) {
                    val existingGoal = DataManager.goals.find { it.category == selectedCategory }
                    if (existingGoal != null) {
                        existingGoal.number = goalNumberInput
                        goalInput.text.clear() // Clear goal number input
                        Toast.makeText(this, "Goal set successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error setting goal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        // Method to add item to the collection
        addItemButton.setOnClickListener {
            try {
                val item = itemInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val date = dateInput.text.toString().trim()

                if (item.isNotBlank() && description.isNotBlank()) {
                    DataManager.addItemToCollection(item, description, date)
                    itemInput.text.clear()
                    descriptionInput.text.clear()
                    dateInput.text.clear()
                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Item and description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error adding item: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        // Apply WindowInsetsListener to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateCategorySpinner() {
        try {
            val categories = DataManager.getAllGoals().map { it.category }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating category spinner: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_view_collections -> {
                startActivity(Intent(this, ViewCollectionsActivity::class.java))
            }
            R.id.nav_sign_out -> {
                Toast.makeText(this, "Sign Out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
