package com.example.anu_news_app.model

data class Category(
    val id: String,
    val title: String,
    val imageResId: Int // Changed from String path to Int resource ID for Android
)
