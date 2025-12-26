package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * SearchActivity: Screen for searching news.
 *
 * It uses a [TextWatcher] to detect user input and performs a search via the API.
 * Defines a 'debounce' mechanism to avoid making API calls for every single keystroke.
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var tvError: android.widget.TextView
    private lateinit var rvSearchNews: androidx.recyclerview.widget.RecyclerView
    
    // Handler to manage delayed execution (Debouncing)
    private val searchHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        
        // Handle window insets for correct padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tilSearch)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Close activity when 'X' icon (optional custom end icon) or Back is pressed
        val tilSearch = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilSearch)
        tilSearch.setEndIconOnClickListener {
            finish()
        }
        
        // Initialize Views
        val etSearch = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearch)
        rvSearchNews = findViewById(R.id.rvSearchNews)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        
        // Setup RecyclerView
        rvSearchNews.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter = NewsAdapter(listOf()) { article ->
             showNewsDialog(article)
        }
        rvSearchNews.adapter = adapter

        // --- TextWatcher for Search Input ---
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // If user is typing, remove any previously scheduled search to prevent multiple calls
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                 val query = s.toString()
                 if (query.isNotEmpty()) {
                     // Prepare a new search task
                     searchRunnable = Runnable {
                         performSearch(query)
                     }
                     // Debounce: Execute 'searchRunnable' after 500ms of inactivity
                     searchHandler.postDelayed(searchRunnable!!, 500)
                 } else {
                     // If Input is empty, clear the list
                     adapter.updateData(listOf())
                 }
            }
        })
    }

    private fun performSearch(query: String) {
        showLoading()
        // Call Search API
        com.example.anu_news_app.api.ApiManager.searchNews(query,
            onSuccess = { newsResponse ->
                runOnUiThread {
                    hideLoading()
                    val articles = newsResponse.articles
                    // if (articles.isNullOrEmpty()) { ... } logic removed
                    showData()
                    adapter.updateData(articles ?: listOf())
                }
            },
            onError = { error ->
                runOnUiThread {
                    hideLoading()
                    showError(error.localizedMessage ?: "Something went wrong")
                }
            }
        )
    }

    // --- UI State Helpers ---

    private fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
        tvError.visibility = android.view.View.GONE
        rvSearchNews.visibility = android.view.View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = android.view.View.GONE
        rvSearchNews.visibility = android.view.View.GONE
        tvError.visibility = android.view.View.VISIBLE
        tvError.text = message
    }

    private fun showData() {
        progressBar.visibility = android.view.View.GONE
        tvError.visibility = android.view.View.GONE
        rvSearchNews.visibility = android.view.View.VISIBLE
    }

    // Note: This dialog code is identical to NewsActivity. 
    // Ideally, this should be refactored into a shared utility function or extension function.
    private fun showNewsDialog(article: com.example.anu_news_app.model.Article) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_news_details)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0))

        val imgDialogNews = dialog.findViewById<android.widget.ImageView>(R.id.imgDialogNews)
        val tvDialogTitle = dialog.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val tvDialogDescription = dialog.findViewById<android.widget.TextView>(R.id.tvDialogDescription)
        val btnViewArticle = dialog.findViewById<android.widget.Button>(R.id.btnViewArticle)

        com.bumptech.glide.Glide.with(this)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_news_placeholder)
            .into(imgDialogNews)
            
        tvDialogTitle.text = article.title
        tvDialogDescription.text = article.description ?: article.content ?: "No content available"

        btnViewArticle.setOnClickListener {
            dialog.dismiss()
            val intent = android.content.Intent(this, WebViewActivity::class.java)
            intent.putExtra("EXTRA_URL", article.url)
            startActivity(intent)
        }

        dialog.show()
    }
    
    private fun hideLoading() {
        progressBar.visibility = android.view.View.GONE
    }
}
