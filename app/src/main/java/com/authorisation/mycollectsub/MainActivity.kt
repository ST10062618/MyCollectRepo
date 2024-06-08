package com.authorisation.mycollectsub

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    private lateinit var cameraOpenId: Button
    lateinit var clickImageId: ImageView

    private lateinit var categoryInput: EditText
    private lateinit var goalInput: EditText
    private lateinit var itemInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addCategoryButton: Button
    private lateinit var addGoalButton: Button
    private lateinit var addItemButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        cameraOpenId = findViewById(R.id.camera_button)
        clickImageId = findViewById(R.id.click_image)

        cameraOpenId.setOnClickListener(View.OnClickListener { v: View? ->
            // Create the camera_intent ACTION_IMAGE_CAPTURE it will open the camera for capture the image
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(cameraIntent, pic_id)
        })

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

        // Populate initial categories into the Spinner
        updateCategorySpinner()

        // Method to add category
        addCategoryButton.setOnClickListener {
            val category = categoryInput.text.toString().trim()
            if (category.isNotBlank()) {
                DataManager.addCategoryWithGoal(category, 0)
                categoryInput.text.clear()
                updateCategorySpinner()
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Category cannot be empty", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Goal set successfully", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Item and description cannot be empty", Toast.LENGTH_SHORT).show()
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
        val categories = DataManager.getAllGoals().map { it.category }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menu navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_view_collections -> {
                startActivity(Intent(this, ViewCollectionsActivity::class.java))
                true
            }
            R.id.nav_sign_out -> {
                startActivity(Intent(this, LogIn::class.java))
                finish() // Optionally, finish this activity so the user can't navigate back to it
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCamera() {
        // Create the camera_intent ACTION_IMAGE_CAPTURE it will open the camera for capture the image
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Start the activity with camera_intent, and request pic id
        startActivityForResult(cameraIntent, pic_id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            val photo = data!!.extras!!["data"] as Bitmap?
            // Set the image in imageview for display
            clickImageId.setImageBitmap(photo)
        }
    }

    companion object {
        private const val pic_id = 123
    }

    private fun handleItemIconClick(collectionItem: CollectionItem) {
        openCamera()
        // You can also update the CollectionItem data with the captured image information here
    }


}
