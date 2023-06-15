package com.example.punchcardv10

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AppPreferences {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    fun init(context: Context, userId: String) {
        firestore = FirebaseFirestore.getInstance()
        this.userId = userId
    }

    fun saveCategories(categories: List<Category>) {
        val categoriesCollection = firestore.collection("users").document(userId).collection("categories")
        val batch = firestore.batch()

        for (category in categories) {
            val categoryDocument = categoriesCollection.document(category.id.toString())
            batch.set(categoryDocument, category, SetOptions.merge())
        }

        batch.commit()
    }

    fun getCategories(callback: (List<Category>) -> Unit) {
        val categoriesCollection = firestore.collection("users").document(userId).collection("categories")
        val categoriesList = mutableListOf<Category>()

        categoriesCollection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val category = document.toObject(Category::class.java)
                    category?.let { categoriesList.add(it) }
                }
                callback(categoriesList)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
    }
}
