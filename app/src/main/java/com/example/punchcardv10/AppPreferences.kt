package com.example.punchcardv10

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AppPreferences {
    private const val PREFERENCES_NAME = "MyAppPreferences"
    private const val KEY_CATEGORIES = "categories"
    private lateinit var preferences: SharedPreferences
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveCategories(categories: List<Category>, userId: String) {
        val sharedPreferences = appContext.getSharedPreferences("MyAppPreferences_$userId", Context.MODE_PRIVATE)
        val jsonString = Gson().toJson(categories)
        sharedPreferences.edit().putString(KEY_CATEGORIES, jsonString).apply()
    }

    fun getCategories(userId: String): List<Category> {
        val sharedPreferences = appContext.getSharedPreferences("MyAppPreferences_$userId", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_CATEGORIES, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Category>>() {}.type
            Gson().fromJson(jsonString, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun getCategoriesKey(userId: String): String {
        return "categories_$userId"
    }
}
