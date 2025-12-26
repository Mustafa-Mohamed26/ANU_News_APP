package com.example.anu_news_app.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the JSON response for a list of news articles from the API.
 * The API returns a structure containing status information and a list of articles.
 *
 * @param status The status of the response (e.g., "ok" or "error").
 * @param totalResults The total number of results available.
 * @param articles The list of [Article] objects found.
 * @param code Error code if request failed.
 * @param message Error description if request failed.
 */
data class NewsResponse(
    // @SerializedName maps the JSON key "status" to the kotlin variable 'status'.
    // Types are nullable (?) because the API might not always return these fields.
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalResults") val totalResults: Int? = null,
    @SerializedName("articles") val articles: List<Article>? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null
)

/**
 * Represents a single news article within the [NewsResponse].
 */
data class Article(
    @SerializedName("source") val source: Source? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("urlToImage") val urlToImage: String? = null,
    @SerializedName("publishedAt") val publishedAt: String? = null,
    @SerializedName("content") val content: String? = null
)
