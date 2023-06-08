package com.example.punchcardv10

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val categoryNameText: TextView = itemView.findViewById(R.id.categoryNameText)
    private val timeGoalText: TextView = itemView.findViewById(R.id.timeGoalText)
    private val colorPreview: ImageView = itemView.findViewById(R.id.colorPreview)

    fun bind(category: Category) {
        categoryNameText.text = category.categoryName
        timeGoalText.text = category.timeGoal
        val color = getCategoryColor(category.selectedColor)
        colorPreview.imageTintList = ColorStateList.valueOf(color)
    }

    private fun getCategoryColor(color: String): Int {
        return try {
            Color.parseColor(color)
        } catch (e: IllegalArgumentException) {
            // Handle invalid color format, return default color
            // For example, return Color.BLACK
            Color.BLACK
        }
    }

}

