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
 * It uses a RecyclerView for the list and TabLayout for filtering by source.
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

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "general"
        findViewById<android.widget.TextView>(R.id.tvCategoryTitle).text = categoryName

        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
            if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }

        rvNews = findViewById(R.id.rvNews)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        tvNoNews = findViewById(R.id.tvNoNews)
        tabLayout = findViewById(R.id.tabLayout)

        rvNews.layoutManager = LinearLayoutManager(this)

        adapter = NewsAdapter(listOf()) { article ->
            showNewsDialog(article)
        }
        rvNews.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val source = tab?.tag as? com.example.anu_news_app.model.Source
                source?.id?.let { getNewsBySourceId(it) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Fetch sources based on category id (lowercase)
        getSources(categoryName.lowercase())

        // --- Drawer Logic ---
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
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            finish()
        }
    }

    private fun getSources(category: String) {
        showLoading()
        com.example.anu_news_app.api.ApiManager.getSources(
            category,
            onSuccess = { sourceResponse ->
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

    private fun setupTabs(sources: List<com.example.anu_news_app.model.Source>) {
        tabLayout.removeAllTabs()
        sources.forEach { source ->
            val tab = tabLayout.newTab()
            tab.text = source.name
            tab.tag = source
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
                         adapter.updateData(articles)
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

    private fun showNewsDialog(article: com.example.anu_news_app.model.Article) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_news_details)
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
        tvDialogDescription.text = article.description ?: article.content ?: "No content available"

        btnViewArticle.setOnClickListener {
            // dialog.dismiss() // Don't dismiss, or dismiss before launch? 
            // Better to dismiss dialog then launch.
            dialog.dismiss()
            val intent = android.content.Intent(this, WebViewActivity::class.java)
            intent.putExtra("EXTRA_URL", article.url)
            startActivity(intent)
        }

        dialog.show()
    }
}
