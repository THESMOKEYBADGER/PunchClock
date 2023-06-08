package com.example.punchcardv10

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.punchcardv10.databinding.ActivityFocusBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FocusActivity : Activity() {

    private lateinit var binding: ActivityFocusBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var timeEntriesList: MutableList<HomeActivity.TimeEntry>
    private lateinit var adapter: TimeEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "") ?: ""
        val timeEntriesListJson = sharedPreferences.getString("timeEntriesList_$userId", null)
        timeEntriesList = if (timeEntriesListJson != null) {
            val type = object : TypeToken<List<HomeActivity.TimeEntry>>() {}.type
            Gson().fromJson<List<HomeActivity.TimeEntry>>(timeEntriesListJson, type)?.toMutableList() ?: mutableListOf()
        } else {
            mutableListOf()
        }
        adapter = TimeEntryAdapter(timeEntriesList)

        val title = intent.getStringExtra("title")
        val dateTime = intent.getStringExtra("dateTime")
        val category = intent.getStringExtra("category")

        binding.focusTitleTextView.text = title
        binding.currentDateTimeTextView.text = dateTime
        binding.categoryFocusView.text = category

        // Handle stop button click
        binding.button.setOnClickListener {
            // Perform necessary actions for stopping the timer and saving the time entry.
            // You can access the title, category, and date/time from the intent extras.

            // Inside the stop button click listener
            val startTime = intent.getLongExtra("startTime", 0L)
            val duration = System.currentTimeMillis() - startTime

            // Update the corresponding time entry in the list with the calculated duration
            val timeEntryId = intent.getStringExtra("timeEntryId")
            val timeEntryIndex = timeEntriesList.indexOfFirst { it.id.toString() == timeEntryId }
            if (timeEntryIndex != -1) {
                val updatedTimeEntry = timeEntriesList[timeEntryIndex].copy(duration = duration)
                timeEntriesList[timeEntryIndex] = updatedTimeEntry
                adapter.notifyItemChanged(timeEntryIndex)

                // Save the updated time entries list to shared preferences
                saveTimeEntriesList()
            }

            // Finish the FocusActivity and return to the HomeActivity.
            finish()
        }
    }

    private fun saveTimeEntriesList() {
        val editor = sharedPreferences.edit()
        val timeEntriesListJson = Gson().toJson(timeEntriesList)
        editor.putString("timeEntriesList_$userId", timeEntriesListJson)
        editor.apply()
    }
}
