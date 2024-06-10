package com.authorisation.mycollectsub

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import java.util.*

// Data class to represent a goal with a category, current count, and target number
data class Goal(val category: String, var number: Int, var targetNumber: Int)

// Data class to represent a collection item with details and an optional image
data class CollectionItem(
    val itemAdded: String,
    val description: String,
    val dateOfAcquisition: String,
    var image: Bitmap? = null
)

// Singleton object to manage data operations for goals and collection items
object DataManager {
    val goals = mutableListOf<Goal>()
    val collection = mutableListOf<CollectionItem>()
    lateinit var progressIndicators: MutableMap<String, ProgressBar>

    // Add a new category with a goal if it doesn't already exist
    fun addCategoryWithGoal(category: String, targetNumber: Int) {
        if (!goals.any { it.category == category }) {
            goals.add(Goal(category, 0, targetNumber))
        }
    }

    // Add an item to the collection and update the corresponding goal's progress
    fun addItemToCollection(item: CollectionItem) {
        collection.add(item)
        val goal = goals.find { it.category == item.itemAdded }
        goal?.let {
            it.number++ // Increment the goal count
            updateProgress(it.category, it.number, it.targetNumber)
        }
    }

    // Retrieve all goals
    fun getAllGoals(): List<Goal> = goals.toList()

    // Retrieve all collection items
    fun getAllItems(): List<CollectionItem> = collection.toList()

    // Update progress of a goal based on current and target values
    private fun updateProgress(category: String, currentValue: Int, targetValue: Int) {
        val progressBar = progressIndicators[category]
        progressBar?.let {
            it.progress = currentValue
        }
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var cameraOpenId: Button
    private lateinit var clickImageId: ImageView
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var categoryInput: EditText
    private lateinit var goalInput: EditText
    private lateinit var itemInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addCategoryButton: Button
    private lateinit var addGoalButton: Button
    private lateinit var addItemButton: Button

    companion object {
        private const val PIC_ID = 123
    }

    private fun initializeProgressBars() {
        val progressContainer = findViewById<LinearLayout>(R.id.progress_container)
        progressContainer.removeAllViews() // Clear previous progress bars

        for (goal in DataManager.getAllGoals()) {
            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = goal.targetNumber
                progress = goal.number
            }
            val textView = TextView(this).apply {
                text = "${goal.category}: ${goal.number}/${goal.targetNumber}"
            }
            progressContainer.addView(textView)
            progressContainer.addView(progressBar)
            DataManager.progressIndicators[goal.category] = progressBar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize UI components and listeners
        initializeUI()
        initializeListeners()
        initializeProgressBars() // Initialize progress bars
        updateCategorySpinner()
    }

    // Initialize UI components by finding views by their IDs
    private fun initializeUI() {
        cameraOpenId = findViewById(R.id.camera_button)
        clickImageId = findViewById(R.id.click_image)
        goalInput = findViewById(R.id.goal_number_input)
        categoryInput = findViewById(R.id.category_input)
        itemInput = findViewById(R.id.item_input)
        descriptionInput = findViewById(R.id.item_description_input)
        dateInput = findViewById(R.id.item_date_input)
        categorySpinner = findViewById(R.id.category_spinner)
        addCategoryButton = findViewById(R.id.add_category_button)
        addGoalButton = findViewById(R.id.add_goal_button)
        addItemButton = findViewById(R.id.add_item_button)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        DataManager.progressIndicators = mutableMapOf()

        // Set padding to avoid overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Initialize listeners for button clicks and other interactive elements
    private fun initializeListeners() {
        cameraOpenId.setOnClickListener { openCamera() }
        addCategoryButton.setOnClickListener { addCategory() }
        addGoalButton.setOnClickListener { setGoal() }
        addItemButton.setOnClickListener { addItem() }
        findViewById<ImageView>(R.id.date_picker_icon).setOnClickListener { showDatePicker() }
    }

    // Add a new category and update the spinner if the category input is not empty
    private fun addCategory() {
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

    // Set a goal for the selected category
    private fun setGoal() {
        val selectedCategory = categorySpinner.selectedItem as String
        val goalNumberInput = goalInput.text.toString().toIntOrNull() ?: 0

        if (selectedCategory.isNotBlank()) {
            val existingGoal = DataManager.goals.find { it.category == selectedCategory }
            existingGoal?.let {
                it.targetNumber = goalNumberInput
                goalInput.text.clear()
                Toast.makeText(this, "Goal set successfully", Toast.LENGTH_SHORT).show()
                initializeProgressBars() // Re-initialize progress bars to update UI
            }
        }
    }

    // Add a new item to the collection and update the relevant fields
    private fun addItem() {
        val item = itemInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val date = dateInput.text.toString().trim()

        val selectedCategory = categorySpinner.selectedItem as? String
        val existingGoal = selectedCategory?.let { category ->
            DataManager.goals.find { it.category == category }
        }

        val photo = (clickImageId.drawable as? BitmapDrawable)?.bitmap
        if (item.isNotBlank() && description.isNotBlank() && date.isNotBlank() && photo != null) {
            if (selectedCategory != null && existingGoal != null) {
                val newItem = CollectionItem(selectedCategory, description, date, photo)
                DataManager.addItemToCollection(newItem)

                itemInput.text.clear()
                descriptionInput.text.clear()
                dateInput.text.clear()
                clickImageId.setImageDrawable(null)

                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
            } else {
                if (selectedCategory == null) {
                    Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                } else if (existingGoal == null) {
                    Toast.makeText(this, "Please set a goal for the selected category", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill all fields and take a photo", Toast.LENGTH_SHORT).show()
        }
    }


    // Open the camera to take a photo
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, PIC_ID)
    }

    // Handle the result from the camera activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            val photo = data?.extras?.get("data") as? Bitmap
            clickImageId.setImageBitmap(photo)
            DataManager.collection.lastOrNull()?.image = photo
        }
    }

    // Show a date picker dialog to select a date
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                dateInput.setText("$year-${month + 1}-$dayOfMonth")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setOnCancelListener {
            dateInput.setText("")
        }
        datePickerDialog.show()
    }

    // Update the category spinner with the current list of categories
    private fun updateCategorySpinner() {
        val categories = DataManager.getAllGoals().map { it.category }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    // Inflate the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Handle item selections from the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_view_collections -> {
                startActivity(Intent(this, ViewCollectionsActivity::class.java))
                true
            }
            R.id.nav_sign_out -> {
                startActivity(Intent(this, LogIn::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
