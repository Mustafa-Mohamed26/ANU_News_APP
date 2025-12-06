package com.example.anu_news_app

/**
 * Data class representing a single news item.
 *
 * @param title The headline of the news.
 * @param author The name of the author or reporter.
 * @param time The time elapsed since publication (e.g., "15 minutes ago").
 * @param imageResId The resource ID for the news image (e.g., R.drawable.sports).
 * @param description The full detail text of the news article.
 */
data class NewsItem(
    val title: String,
    val author: String,
    val time: String,
    val imageResId: Int,
    val description: String
)
