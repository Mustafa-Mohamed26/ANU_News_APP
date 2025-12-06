package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class NewsActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)
        
        // Use drawerLayout as the visual root for edge-to-edge
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

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "General"
        findViewById<android.widget.TextView>(R.id.tvCategoryTitle).text = categoryName

        // 1. Drawer Toggle (was imgBack, now imgMenu conceptually, but ID changed in XML to imgMenu)
        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
             if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        // Search Button
        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }

        // Setup RecyclerView
        val rvNews = findViewById<RecyclerView>(R.id.rvNews)
        rvNews.layoutManager = LinearLayoutManager(this)
        adapter = NewsAdapter(generateDummyNews(categoryName))
        rvNews.adapter = adapter

        // Setup TabLayout (Sources)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        for (i in 1..10) {
            tabLayout.addTab(tabLayout.newTab().setText("$categoryName $i"))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Shuffle/Change data based on source
                val sourceName = tab?.text.toString()
                // Update to pass just the source name or combined, existing logic handles string concat
                adapter.updateData(generateDummyNews("$sourceName"))
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // --- Drawer Logic ---

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
            // Clear SharedPreferences
            sharedPreferences.edit().clear().apply()

            // Navigate back to MainActivity (Login/Register)
            val intent = android.content.Intent(this, MainActivity::class.java)
            // Clear back stack so user can't go back
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 4. "Go To Home" Logic
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            // Since we are in NewsActivity, going to Home means finishing this activity
            finish()
        }
    }

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
                    imageResId = imageResId
                )
            )
        }
        
        return list
    }
}
