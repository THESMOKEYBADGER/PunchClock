package com.example.punchcardv10

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CategoryAdapter(
    private val categories: MutableList<Category>,
    private val categoryDeleteListener: CategoryDeleteListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface CategoryDeleteListener {
        fun onDeleteCategory(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameText: TextView = itemView.findViewById(R.id.categoryNameText)
        private val timeGoalText: TextView = itemView.findViewById(R.id.timeGoalText)
        private val deleteButton: Button = itemView.findViewById(R.id.catDelete)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = categories[position]
                    categoryDeleteListener.onDeleteCategory(category)
                }
            }
        }

        fun bind(category: Category) {
            categoryNameText.text = category.categoryName
            timeGoalText.text = category.timeGoal
        }
    }
}
