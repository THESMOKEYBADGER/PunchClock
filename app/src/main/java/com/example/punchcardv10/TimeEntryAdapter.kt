package com.example.punchcardv10

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class TimeEntryAdapter(private val timeEntries: ArrayList<UserActivity>) :
    RecyclerView.Adapter<TimeEntryAdapter.TimeEntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeEntryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_entry_item, parent, false)
        return TimeEntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeEntryViewHolder, position: Int) {
        val timeEntry = timeEntries[position]
        holder.bind(timeEntry)
    }

    override fun getItemCount(): Int {
        return timeEntries.size
    }

    inner class TimeEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.time_entry_title)
        private val categoryCircle: View = itemView.findViewById(R.id.categoryCircle)
        private val durationTextView: TextView = itemView.findViewById(R.id.time_entry_duration)
        private val deleteButton: Button = itemView.findViewById(R.id.itemDel)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    timeEntries.removeAt(position)
                    notifyItemRemoved(position)
                    // Save the updated time entries list to shared preferences
                    saveTimeEntriesList()
                }
            }
        }

        fun bind(timeEntry: UserActivity) {
            titleTextView.text = timeEntry.title
            categoryCircle.backgroundTintList = getColorStateListByCategory(timeEntry.category)
            durationTextView.text = formatDuration(timeEntry.duration)
        }

        private fun getColorStateListByCategory(category: String): ColorStateList? {
            val categoryColor = getCategoryColor(category)
            return if (categoryColor != null) {
                ColorStateList.valueOf(categoryColor)
            } else {
                null
            }
        }

        private fun getCategoryColor(category: String): Int? {
            return when (category) {
                "Category 1" -> Color.RED
                "Category 2" -> Color.GREEN
                "Category 3" -> Color.BLUE
                else -> null
            }
        }

        private fun formatDuration(duration: Long): String {
            val hours = TimeUnit.MILLISECONDS.toHours(duration)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    private fun saveTimeEntriesList() {
        // Save the updated time entries list to shared preferences
    }
}
