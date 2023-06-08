package com.example.punchcardv10

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.punchcardv10.databinding.ActivityHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : Activity() {

    data class TimeEntry(
        val id: UUID = UUID.randomUUID(),
        val title: String,
        val category: String,
        val startTime: Long,
        val duration: Long
    )

    private lateinit var binding: ActivityHomeBinding
    private lateinit var timeEntriesRecyclerView: RecyclerView
    private lateinit var timeEntriesList: MutableList<TimeEntry>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: TimeEntryAdapter

    private lateinit var categories: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Initialize AppPreferences
        AppPreferences.init(this)

        // Get the user ID from the shared preferences
        userId = sharedPreferences.getString("userId", "") ?: ""

        // Initialize the time entries list and adapter
        timeEntriesList = mutableListOf()
        layoutManager = LinearLayoutManager(this)
        adapter = TimeEntryAdapter(timeEntriesList)

        // Set up the time entries recycler view
        timeEntriesRecyclerView = binding.timeEntriesRecyclerView
        timeEntriesRecyclerView.layoutManager = layoutManager
        timeEntriesRecyclerView.adapter = adapter

        // Set current date and time
        val currentDateTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        binding.currentDateTimeTextView.text = currentDateTime

        // Get categories from AppPreferences
        categories = AppPreferences.getCategories(userId).map { it.categoryName }

        // Populate category spinner with data
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        // Handle punch-in button click
        binding.punchInButton.setOnClickListener {
            val selectedCategory = binding.categorySpinner.selectedItem?.toString()
            val title = binding.editTextText.text?.toString()

            if (!title.isNullOrEmpty() && selectedCategory != null) {
                // Create a new TimeEntry object with a unique ID and start time
                val startTime = System.currentTimeMillis()
                val timeEntry = TimeEntry(
                    title = title,
                    category = selectedCategory,
                    startTime = startTime,
                    duration = 0L
                )

                // Add the time entry to the list
                timeEntriesList.add(timeEntry)
                adapter.notifyItemInserted(timeEntriesList.size - 1)

                // Start FocusActivity and pass the title, category, start time, and time entry ID as extras
                val intent = Intent(this@HomeActivity, FocusActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("category", selectedCategory)
                intent.putExtra("startTime", startTime)
                intent.putExtra("timeEntryId", timeEntry.id.toString())
                startActivity(intent)

                // Clear the fields or perform any additional actions after starting the timer.
                binding.editTextText.text?.clear()

                // Save the updated time entries list to shared preferences
                saveTimeEntriesList()
            }
        }

        val timeEntriesListJson = sharedPreferences.getString("timeEntriesList_$userId", null)
        timeEntriesList = if (timeEntriesListJson != null) {
            val type = object : TypeToken<List<TimeEntry>>() {}.type
            Gson().fromJson<List<TimeEntry>>(timeEntriesListJson, type)?.toMutableList() ?: mutableListOf()
        } else {
            mutableListOf()
        }

        // Update the adapter with the updated time entries list
        adapter = TimeEntryAdapter(timeEntriesList)
        timeEntriesRecyclerView.adapter = adapter

        val categoryButton: ImageButton = binding.buttonCategory
        categoryButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Save the time entries list to shared preferences
        saveTimeEntriesList()
    }

    private fun saveTimeEntriesList() {
        val editor = sharedPreferences.edit()
        val timeEntriesListJson = Gson().toJson(timeEntriesList)
        editor.putString("timeEntriesList_$userId", timeEntriesListJson)
        editor.apply()
    }
}
