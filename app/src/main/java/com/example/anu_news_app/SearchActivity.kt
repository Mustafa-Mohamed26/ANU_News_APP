package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * SearchActivity: Screen for searching news.
 * Currently serves as a UI placeholder logic (input field + back button).
 */
class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        
        // Edge-to-Edge handling for the search input container
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tilSearch)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle "X" icon click in the search bar
        val tilSearch = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilSearch)
        tilSearch.setEndIconOnClickListener {
            // finish() closes the current activity and returns to the previous one (HomeActivity).
            finish()
        }
    }
}
