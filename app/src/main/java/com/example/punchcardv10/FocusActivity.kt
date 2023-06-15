import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.punchcardv10.TimeEntryAdapter
import com.example.punchcardv10.UserActivity
import com.example.punchcardv10.databinding.ActivityFocusBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class FocusActivity : Activity() {

    private lateinit var binding: ActivityFocusBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var timeEntriesList: ArrayList<UserActivity>
    private lateinit var adapter: TimeEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "") ?: ""
        val timeEntriesListJson = sharedPreferences.getString("timeEntriesList_$userId", null)
        timeEntriesList = if (timeEntriesListJson != null) {
            val type = object : TypeToken<ArrayList<UserActivity>>() {}.type
            Gson().fromJson<ArrayList<UserActivity>>(timeEntriesListJson, type) ?: ArrayList()
        } else {
            ArrayList()
        }
        adapter = TimeEntryAdapter(timeEntriesList)

        val title = intent.getStringExtra("title") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val dateTime = intent.getStringExtra("dateTime")


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

            // Create a new UserActivity object
            val timeEntry = UserActivity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                startTime = startTime,
                duration = duration
            )

            // Add the time entry to the list
            timeEntriesList.add(timeEntry)
            adapter.notifyItemInserted(timeEntriesList.size - 1)

            // Save the updated time entries list to shared preferences
            saveTimeEntriesList()

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
