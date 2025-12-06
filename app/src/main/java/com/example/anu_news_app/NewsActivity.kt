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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)
        
        // Use drawerLayout as the visual root for edge-to-edge
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val mainContent = findViewById<android.view.View>(R.id.main)

        // Reset padding for DrawerLayout to avoid double padding/margins
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
           v.setPadding(0, 0, 0, 0)
           insets
        }

        // Apply system bars insets to main content
        ViewCompat.setOnApplyWindowInsetsListener(mainContent) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the category name passed from HomeActivity
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "General"
        findViewById<android.widget.TextView>(R.id.tvCategoryTitle).text = categoryName

        // 1. Drawer Toggle
        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
             if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        // Search Button
        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }

        // Setup RecyclerView (The list of news)
        val rvNews = findViewById<RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this) // Linear layout = vertical list
        
        // Initialize Adapter with dummy data and a click listener callback
        adapter = NewsAdapter(generateDummyNews(categoryName)) { newsItem ->
            showNewsDialog(newsItem)
        }
        rvNews.adapter = adapter

        // Setup TabLayout (The horizontal bar for Sources)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        // Add 10 dummy sources
        for (i in 1..10) {
            tabLayout.addTab(tabLayout.newTab().setText("$categoryName $i"))
        }

        // Listen for tab selections to filter news
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Shuffle/Change data based on source
                val sourceName = tab?.text.toString()
                // Retrieve new data and update the adapter
                adapter.updateData(generateDummyNews("$sourceName"))
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // --- Drawer Logic (Same as HomeActivity) ---

        // 2. Populate User Data
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "email@example.com")
        val userPhone = sharedPreferences.getString("userPhone", "No Phone")

        findViewById<android.widget.TextView>(R.id.tvDrawerName).text = userName
        findViewById<android.widget.TextView>(R.id.tvDrawerEmail).text = userEmail
        findViewById<android.widget.TextView>(R.id.tvDrawerPhone).text = userPhone

        // 3. Logout Logic
        findViewById<android.view.View>(R.id.btnLogout).setOnClickListener {
            sharedPreferences.edit().clear().apply()

            val intent = android.content.Intent(this, MainActivity::class.java)
            // Clear back stack
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 4. "Go To Home" Logic
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            // Since we are in NewsActivity, going to Home means just closing this one
            finish()
        }
    }

    /**
     * Displays a custom Dialog with news details.
     */
    private fun showNewsDialog(newsItem: NewsItem) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_news_details)
        
        // Make background transparent for CardView radius to show correctly
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0))

        val imgDialogNews = dialog.findViewById<android.widget.ImageView>(R.id.imgDialogNews)
        val tvDialogTitle = dialog.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val tvDialogDescription = dialog.findViewById<android.widget.TextView>(R.id.tvDialogDescription)
        val btnViewArticle = dialog.findViewById<android.widget.Button>(R.id.btnViewArticle)

        // Populate dialog views with data from the clicked item
        imgDialogNews.setImageResource(newsItem.imageResId)
        tvDialogTitle.text = newsItem.title
        tvDialogDescription.text = newsItem.description

        btnViewArticle.setOnClickListener {
            dialog.dismiss()
            // In a real app, this would open a WebView or Browser
        }

        dialog.show()
    }

    /**
     * Helper to create dummy news data for testing.
     */
    private fun generateDummyNews(context: String): List<NewsItem> {
        val list = mutableListOf<NewsItem>()
        val images = listOf(
            R.drawable.general,
            R.drawable.business,
            R.drawable.sports,
            R.drawable.technology,
            R.drawable.entertainment,
            R.drawable.health,
            R.drawable.science
        )

        for (i in 1..10) {
            val imageResId = images[(i - 1) % images.size]
            list.add(
                NewsItem(
                    title = "News $i for $context source",
                    author = "By : Author $i",
                    time = "$i hours ago",
                    imageResId = imageResId,
                    description = "A 40-year-old man has fallen approximately 200 feet to his death while canyoneering with three others at Zion National Park in Utah, authorities confirmed. \\n\\nThe incident occurred on Saturday when the... [+1529 chars]"
                )
            )
        }
        
        return list
    }
}
