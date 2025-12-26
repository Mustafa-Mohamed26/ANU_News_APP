package com.example.anu_news_app.api

/**
 * Singleton object holding constant values for API configuration.
 *
 * Using an object makes these constants static and globally accessible.
 */
object ApiConstants {
    // The base domain of the API
    const val BASE_URL = "newsapi.org"
    
    // Endpoint to fetch news sources (e.g., BBC, CNN)
    const val SOURCE_API = "/v2/top-headlines/sources"
    
    // Endpoint to fetch news articles
    const val NEWS_API = "/v2/everything"
    
    // API Key for authentication (should ideally be in local.properties for security, but keeping here for simplicity)
    const val API_KEY = "c858080dba1e49138258b58e8c349a5e"
}
