package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        
        // Use drawerLayout as the visual root for edge-to-edge
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val mainContent = findViewById<android.view.View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply insets to the drawer layout (so it draws behind bars if needed, but we handle padding)
            // Actually, usually we want the padding on the content, not the drawer container
            // Let's keep the padding logic on the main content part
            v.setPadding(0, 0, 0, 0) 
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainContent) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val padding = android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 
                24f, 
                resources.displayMetrics
            ).toInt()
            
            v.setPadding(
                systemBars.left + padding, 
                systemBars.top + padding, 
                systemBars.right + padding, 
                systemBars.bottom + padding
            )
            insets
        }

        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            val intent = android.content.Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // 1. Setup Drawer Toggle
        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
            if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

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
            // Clear back stack so user can't go back to Home
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 4. "Go To Home" Logic (Closes drawer)
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        // 5. Category Click Listeners
        setupCategory(R.id.imgGeneral, "General")
        setupCategory(R.id.imgBusiness, "Business")
        setupCategory(R.id.imgSports, "Sports")
        setupCategory(R.id.imgTechnology, "Technology")
        setupCategory(R.id.imgEntertainment, "Entertainment")
        setupCategory(R.id.imgHealth, "Health")
        setupCategory(R.id.imgScience, "Science")
    }

    private fun setupCategory(viewId: Int, categoryName: String) {
        findViewById<android.view.View>(viewId).setOnClickListener {
            val intent = android.content.Intent(this, NewsActivity::class.java)
            intent.putExtra("CATEGORY_NAME", categoryName)
            startActivity(intent)
        }
    }
}
