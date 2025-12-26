package com.example.anu_news_app.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the JSON response for a list of news sources.
 *
 * @param status Response status ("ok" or "error").
 * @param sources List of [Source] objects.
 */
data class SourceResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("sources") val sources: List<Source>? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null
)

/**
 * Represents a news publisher/source (e.g., "BBC News").
 * This class is reused in both [SourceResponse] and [Article].
 */
data class Source(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("country") val country: String? = null
)
