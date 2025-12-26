package com.example.anu_news_app.api

import android.util.Log
import com.example.anu_news_app.model.NewsResponse
import com.example.anu_news_app.model.SourceResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object ApiManager {
    // OkHttpClient handles the network requests (connection pooling, timeouts, etc.)
    private val client = OkHttpClient()
    // Gson is used to convert JSON strings into Kotlin objects
    private val gson = Gson()

    /**
     * Fetches the list of news sources (publishers) based on a category.
     *
     * @param categoryID The category ID (e.g., "sports").
     * @param onSuccess Callback function invoked when data is successfully fetched and parsed.
     * @param onError Callback function invoked when the network call fails or parsing error occurs.
     */
    fun getSources(
        categoryID: String,
        onSuccess: (SourceResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // Build the full URL: https://newsapi.org/v2/top-headlines/sources?apiKey=...&category=...
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(ApiConstants.BASE_URL)
            .addPathSegments(ApiConstants.SOURCE_API.trimStart('/')) // Ensure no double slashes
            .addQueryParameter("apiKey", ApiConstants.API_KEY)
            .addQueryParameter("category", categoryID)

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        // Execute the request asynchronously on a background thread
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Network failure (e.g., no internet, timeout)
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        // Read the response body as a string
                        val responseBody = response.body?.string()
                        
                        // Parse JSON parsing: String -> SourceResponse object
                        val sourceResponse = gson.fromJson(responseBody, SourceResponse::class.java)
                        
                        // Return the result via the success callback
                        onSuccess(sourceResponse)
                    } catch (e: Exception) {
                        // Handle parsing errors
                        onError(e)
                    }
                } else {
                    // API returned an error code (e.g., 401 Unauthorized, 500 Server Error)
                    onError(IOException("Error: ${response.code}"))
                }
            }
        })
    }

    /**
     * Fetches news articles from a specific source.
     *
     * @param sourceId The ID of the news source (e.g., "bbc-news").
     * @param searchQuery Optional keyword to filter results (e.g., "bitcoin").
     */
    fun getNewsBySourceId(
        sourceId: String,
        searchQuery: String? = null,
        onSuccess: (NewsResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(ApiConstants.BASE_URL)
            .addPathSegments(ApiConstants.NEWS_API.trimStart('/'))
            .addQueryParameter("apiKey", ApiConstants.API_KEY)
            .addQueryParameter("sources", sourceId)

        if (!searchQuery.isNullOrEmpty()) {
            urlBuilder.addQueryParameter("q", searchQuery)
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()
        
        Log.e("ApiManager", "url: ${urlBuilder.build()}")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string()
                        val newsResponse = gson.fromJson(responseBody, NewsResponse::class.java)
                        onSuccess(newsResponse)
                    } catch (e: Exception) {
                        onError(e)
                    }
                } else {
                    onError(IOException("Error: ${response.code}"))
                }
            }
        })
    }

    /**
     * Searches for news articles based on a query string globally (not restricted to a source).
     *
     * @param query The search keyword.
     */
    fun searchNews(
        query: String,
        onSuccess: (NewsResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(ApiConstants.BASE_URL)
            .addPathSegments(ApiConstants.NEWS_API.trimStart('/'))
            .addQueryParameter("apiKey", ApiConstants.API_KEY)
            .addQueryParameter("q", query)

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string()
                        val newsResponse = gson.fromJson(responseBody, NewsResponse::class.java)
                        onSuccess(newsResponse)
                    } catch (e: Exception) {
                        onError(e)
                    }
                } else {
                    onError(IOException("Error: ${response.code}"))
                }
            }
        })
    }
}
