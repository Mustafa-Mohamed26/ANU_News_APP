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
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getSources(
        categoryID: String,
        onSuccess: (SourceResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(ApiConstants.BASE_URL)
            .addPathSegments(ApiConstants.SOURCE_API.trimStart('/')) // api constants has leading /
            .addQueryParameter("apiKey", ApiConstants.API_KEY)
            .addQueryParameter("category", categoryID)

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
                        val sourceResponse = gson.fromJson(responseBody, SourceResponse::class.java)
                        onSuccess(sourceResponse)
                    } catch (e: Exception) {
                        onError(e)
                    }
                } else {
                    onError(IOException("Error: ${response.code}"))
                }
            }
        })
    }

    fun getNewsBySourceId(
        sourceId: String,
        searchQuery: String? = null,
        onSuccess: (NewsResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(ApiConstants.BASE_URL)
            .addPathSegments(ApiConstants.NEWS_API.trimStart('/')) // remove leading / if generic
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
