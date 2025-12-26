package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

/**
 * NewsActivity: Displays a list of news articles for a selected category.
 *
 * This Activity is responsible for:
 * 1. Receiving the category name from [HomeActivity].
 * 2. Fetching the list of news sources (tabs) using [ApiManager].
 * 3. Fetching news articles for the selected source.
 * 4. Displaying articles in a [RecyclerView].
 * 5. Handling user interactions (clicking an article).
 */
class NewsActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var tvError: android.widget.TextView
    private lateinit var tvNoNews: android.widget.TextView
    private lateinit var rvNews: RecyclerView
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)

        // --- Setup Window Insets (Padding) ---
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val mainContent = findViewById<android.view.View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainContent) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Retrieve Data from Intent ---
        // We get the string passed from HomeActivity. If null, default to "general".
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "general"
        findViewById<android.widget.TextView>(R.id.tvCategoryTitle).text = categoryName

        // --- Setup Drawer Toggle ---
        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
            if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }

        // --- Setup Views ---
        rvNews = findViewById(R.id.rvNews)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvNoNews = findViewById(R.id.tvNoNews)
        tabLayout = findViewById(R.id.tabLayout)

        // Setup RecyclerView
        rvNews.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with an empty list initially.
        // The lambda { article -> ... } is the onItemClicked callback.
        adapter = NewsAdapter(listOf()) { article ->
            showNewsDialog(article)
        }
        rvNews.adapter = adapter

        // --- Setup TabLayout Listeners ---
        // This handles what happens when a user clicks on a Source tab (e.g., "BBC").
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Determine which source was selected using the 'tag' property
                val source = tab?.tag as? com.example.anu_news_app.model.Source
                // If source has an ID, fetch its news
                source?.id?.let { getNewsBySourceId(it) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Fetch sources based on category id (lowercase) - Starts the data loading process
        getSources(categoryName.lowercase())

        // --- Drawer User Info & Logout (Duplicated logic from HomeActivity) ---
        // In a production app, we might move this to a BaseActivity to avoid duplication.
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "email@example.com")
        val userPhone = sharedPreferences.getString("userPhone", "No Phone")

        findViewById<android.widget.TextView>(R.id.tvDrawerName).text = userName
        findViewById<android.widget.TextView>(R.id.tvDrawerEmail).text = userEmail
        findViewById<android.widget.TextView>(R.id.tvDrawerPhone).text = userPhone

        findViewById<android.view.View>(R.id.btnLogout).setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = android.content.Intent(this, MainActivity::class.java)
            // Clear back stack so user can't go back to NewsActivity after logging out
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            finish() // Just close this activity to go back to HomeActivity
        }
    }

    /**
     * Fetches the list of Sources (publishers) for the current category.
     */
    private fun getSources(category: String) {
        showLoading()
        // API Call
        com.example.anu_news_app.api.ApiManager.getSources(
            category,
            onSuccess = { sourceResponse ->
                // API calls happen on a background thread.
                // We MUST switch to the UI thread (runOnUiThread) to update Views (TabLayout, etc.).
                runOnUiThread {
                    // Sources loaded, we don't hide loading yet, we wait for news
                    val sources = sourceResponse.sources
                    if (sources.isNullOrEmpty()) {
                        hideLoading()
                        showError("No sources found")
                    } else {
                         // We don't hide loading here because we immediately fetch news for the first source
                        setupTabs(sources)
                    }
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

    /**
     * Creating tabs dynamically based on the list of sources retrieved.
     */
    private fun setupTabs(sources: List<com.example.anu_news_app.model.Source>) {
        tabLayout.removeAllTabs()
        sources.forEach { source ->
            val tab = tabLayout.newTab()
            tab.text = source.name
            tab.tag = source // Associate the Source object with the tab
            tabLayout.addTab(tab)
        }
        // Select first tab automatically if available
        if (tabLayout.tabCount > 0) {
            val firstTab = tabLayout.getTabAt(0)
            firstTab?.select()
            // Manually trigger news fetch for first tab
            val firstSource = firstTab?.tag as? com.example.anu_news_app.model.Source
            firstSource?.id?.let { getNewsBySourceId(it) }
        } else {
             hideLoading()
        }
    }

    /**
     * Fetches news articles for a specific source ID.
     */
    private fun getNewsBySourceId(sourceId: String) {
        showLoading()
        // Clear current list while loading
        adapter.updateData(listOf())

        com.example.anu_news_app.api.ApiManager.getNewsBySourceId(
            sourceId,
            onSuccess = { newsResponse ->
                runOnUiThread {
                    hideLoading()
                    val articles = newsResponse.articles
                    if (articles.isNullOrEmpty()) {
                         showEmpty()
                    } else {
                         showData()
                         adapter.updateData(articles) // Update adapter with new data
                    }
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

    // Helper functions to manage UI State (Loading, Error, Empty, Content)
    
    private fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
        tvError.visibility = android.view.View.GONE
        tvNoNews.visibility = android.view.View.GONE
        rvNews.visibility = android.view.View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = android.view.View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = android.view.View.GONE
        rvNews.visibility = android.view.View.GONE
        tvNoNews.visibility = android.view.View.GONE
        tvError.visibility = android.view.View.VISIBLE
        tvError.text = message
    }

    private fun showEmpty() {
        progressBar.visibility = android.view.View.GONE
        rvNews.visibility = android.view.View.GONE
        tvError.visibility = android.view.View.GONE
        tvNoNews.visibility = android.view.View.VISIBLE
    }

    private fun showData() {
        progressBar.visibility = android.view.View.GONE
        tvError.visibility = android.view.View.GONE
        tvNoNews.visibility = android.view.View.GONE
        rvNews.visibility = android.view.View.VISIBLE
    }

    /**
     * Displays a customized Dialog when an item is clicked.
     */
    private fun showNewsDialog(article: com.example.anu_news_app.model.Article) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_news_details)
        // Make dialog background transparent so our rounded corners show nicely
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0))

        val imgDialogNews = dialog.findViewById<android.widget.ImageView>(R.id.imgDialogNews)
        val tvDialogTitle = dialog.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val tvDialogDescription = dialog.findViewById<android.widget.TextView>(R.id.tvDialogDescription)
        val btnViewArticle = dialog.findViewById<android.widget.Button>(R.id.btnViewArticle)

        // Load image using Glide
        com.bumptech.glide.Glide.with(this)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_news_placeholder)
            .into(imgDialogNews)

        tvDialogTitle.text = article.title
        // Show description, or content, or fallback text if both null
        tvDialogDescription.text = article.description ?: article.content ?: "No content available"

        btnViewArticle.setOnClickListener {
            dialog.dismiss()
            // Launch WebViewActivity to read full article
            val intent = android.content.Intent(this, WebViewActivity::class.java)
            intent.putExtra("EXTRA_URL", article.url)
            startActivity(intent)
        }

        dialog.show()
    }
}
