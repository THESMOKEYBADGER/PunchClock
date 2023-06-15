import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.punchcardv10.AppPreferences
import com.example.punchcardv10.Category
import com.example.punchcardv10.CategoryActivity
import com.example.punchcardv10.TimeEntryAdapter
import com.example.punchcardv10.UserActivity
import com.example.punchcardv10.databinding.ActivityHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : Activity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var timeEntriesRecyclerView: RecyclerView
    private lateinit var userActivitiesList: ArrayList<UserActivity>
    private lateinit var categoriesList: ArrayList<Category>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: TimeEntryAdapter

    data class TimeEntry(
        val title: String,
        val category: String,
        val startTime: Long,
        val duration: Long
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "") ?: ""

        // Initialize the user activities list and adapter
        userActivitiesList = ArrayList()
        layoutManager = LinearLayoutManager(this)
        adapter = TimeEntryAdapter(userActivitiesList)

        // Set up the time entries recycler view
        timeEntriesRecyclerView = binding.timeEntriesRecyclerView
        timeEntriesRecyclerView.layoutManager = layoutManager
        timeEntriesRecyclerView.adapter = adapter

        // Set current date and time
        val currentDateTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        binding.currentDateTimeTextView.text = currentDateTime

        AppPreferences.init(applicationContext, userId)

        AppPreferences.getCategories { categories ->
            categoriesList = ArrayList(categories)

            // Populate category spinner with data
            val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.categoryName })
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = categoryAdapter
        }

        // Handle punch-in button click
        binding.punchInButton.setOnClickListener {
            val selectedCategory = binding.categorySpinner.selectedItem?.toString()
            val title = binding.editTextText.text?.toString()

            if (!title.isNullOrEmpty() && selectedCategory != null) {
                // Create a new UserActivity object with a unique ID and start time
                val startTime = System.currentTimeMillis()
                val userActivity = UserActivity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    category = selectedCategory,
                    startTime = startTime,
                    duration = 0L
                )

                // Add the user activity to the list
                userActivitiesList.add(userActivity)
                adapter.notifyItemInserted(userActivitiesList.size - 1)

                // Start FocusActivity and pass the title, category, start time, and user activity ID as extras
                val intent = Intent(this@HomeActivity, FocusActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("category", selectedCategory)
                intent.putExtra("startTime", startTime)
                intent.putExtra("userActivityId", userActivity.id)
                startActivity(intent)

                // Clear the fields or perform any additional actions after starting the timer.
                binding.editTextText.text?.clear()

                // Save the updated user activities list to shared preferences
                saveUserActivitiesList()
            }
        }

        // Load user activities from shared preferences
        loadUserActivitiesList()

        // Update the adapter with the loaded user activities list
        adapter = TimeEntryAdapter(userActivitiesList)
        timeEntriesRecyclerView.adapter = adapter

        val categoryButton: ImageButton = binding.buttonCategory
        categoryButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Save the user activities list to shared preferences
        saveUserActivitiesList()
    }

    private fun saveUserActivitiesList() {
        val editor = sharedPreferences.edit()
        val userActivitiesListJson = Gson().toJson(userActivitiesList)
        editor.putString("userActivitiesList_$userId", userActivitiesListJson)
        editor.apply()
    }

    private fun loadUserActivitiesList() {
        val userActivitiesListJson = sharedPreferences.getString("userActivitiesList_$userId", null)
        userActivitiesList = if (userActivitiesListJson != null) {
            val type = object : TypeToken<ArrayList<UserActivity>>() {}.type
            Gson().fromJson<ArrayList<UserActivity>>(userActivitiesListJson, type) ?: ArrayList()
        } else {
            ArrayList()
        }
    }
}
