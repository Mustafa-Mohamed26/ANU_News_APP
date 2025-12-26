package com.example.anu_news_app.model

/**
 * Represents a news category (e.g., Sports, Technology).
 *
 * This data class is used to model the categories displayed in the UI.
 *
 * @property id The unique identifier for the category (used for API calls, e.g., "sports").
 * @property title The display name of the category (e.g., "Sports").
 * @property imageResId The drawable resource ID for the category's background image.
 */
data class Category(
    val id: String,
    val title: String,
    val imageResId: Int // References a drawable resource (R.drawable.xxx)
)
