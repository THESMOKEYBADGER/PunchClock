package com.example.punchcardv10

data class UserActivity(
    val id: String,
    val title: String,
    val category: String,
    val startTime: Long,
    val duration: Long
)