package com.example.punchcardv10

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryActivity : Activity(), CategoryAdapter.CategoryDeleteListener {
    private lateinit var colorSpinner: Spinner
    private lateinit var colorPreview: ImageView
    private lateinit var categoryNameEditText: EditText
    private lateinit var stampButton: Button
    private lateinit var timeGoalEditText: EditText
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var userId: String

    private val categoriesList: ArrayList<Category> = ArrayList()
    private var categoryIdCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        colorSpinner = findViewById(R.id.colorSpinner)
        colorPreview = findViewById(R.id.colorPreview)
        categoryNameEditText = findViewById(R.id.categoryNameEditText)
        stampButton = findViewById(R.id.stampButton)
        timeGoalEditText = findViewById(R.id.timeGoalEditText)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)

        // Get the user ID from the shared preferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "") ?: ""

        AppPreferences.init(applicationContext, userId)

        AppPreferences.getCategories { categories ->
            categoriesList.addAll(categories)
            categoryAdapter.notifyDataSetChanged()
        }

        categoryAdapter = CategoryAdapter(categoriesList, this)
        categoriesRecyclerView.adapter = categoryAdapter
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        val colors = arrayOf(
            "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Brown", "Black"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = adapter

        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedColor = colors[position]
                val colorCode = getColorCode(selectedColor)
                colorPreview.setBackgroundColor(colorCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when nothing is selected
            }
        }

        stampButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()
            val selectedColor = colors[colorSpinner.selectedItemPosition]
            val timeGoal = timeGoalEditText.text.toString()

            val category = Category(categoryIdCounter, categoryName, selectedColor, timeGoal)
            categoriesList.add(category)
            categoryAdapter.notifyItemInserted(categoriesList.size - 1)
            categoryIdCounter++

            AppPreferences.saveCategories(categoriesList)

            clearFields()
        }

        val homeButton: ImageButton = findViewById(R.id.button_home)
        homeButton.setOnClickListener {
            val intent = Intent(this@CategoryActivity, HomeActivity::class.java)
            intent.putStringArrayListExtra("categories", ArrayList(categoriesList.map { it.categoryName }))
            startActivity(intent)
        }
    }

    override fun onDeleteCategory(category: Category) {
        categoriesList.remove(category)
        categoryAdapter.notifyDataSetChanged()

        AppPreferences.saveCategories(categoriesList)
    }

    private fun getColorCode(color: String): Int {
        return when (color) {
            "Red" -> Color.RED
            "Orange" -> Color.parseColor("#FFA500")
            "Yellow" -> Color.YELLOW
            "Green" -> Color.GREEN
            "Blue" -> Color.BLUE
            "Indigo" -> Color.parseColor("#4B0082")
            "Violet" -> Color.parseColor("#EE82EE")
            "Brown" -> Color.parseColor("#A52A2A")
            "Black" -> Color.BLACK
            else -> Color.TRANSPARENT
        }
    }

    private fun clearFields() {
        categoryNameEditText.text.clear()
        colorSpinner.setSelection(0)
        timeGoalEditText.text.clear()
    }
}
