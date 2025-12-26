package com.example.anu_news_app

/**
 * Data class representing a single news item for UI display.
 *
 * Note: This is different from the [com.example.anu_news_app.model.Article] class.
 * The `Article` class mirrors the API JSON structure, while this `NewsItem` class
 * is likely used for a custom/dummy implementation or a mapped UI state.
 *
 * @param title The headline of the news.
 * @param author The name of the author or reporter.
 * @param time The time elapsed since publication (e.g., "15 minutes ago").
 * @param imageResId The resource ID for the locally stored image (not a URL).
 * @param description The full detail text of the news article.
 */
data class NewsItem(
    val title: String,
    val author: String,
    val time: String,
    val imageResId: Int,
    val description: String
)
