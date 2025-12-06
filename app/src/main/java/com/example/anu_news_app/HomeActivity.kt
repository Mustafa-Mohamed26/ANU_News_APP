package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * HomeActivity: The main dashboard of the application.
 * 
 * Responsibilities:
 * 1. Displaying News Categories (General, Sports, etc.).
 * 2. Handling Navigation via a Side Drawer (DrawerLayout).
 * 3. Greeting the user with their name.
 */
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        
        // --- View Setup ---
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val mainContent = findViewById<android.view.View>(R.id.main)

        // 1. Window Insets for Drawer
        // We set padding to 0 here because the Drawer usually handles its own insets or draws full screen.
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            v.setPadding(0, 0, 0, 0) 
            insets
        }

        // 2. Window Insets for Main Content
        // We add padding here to respect system bars + our own 24dp margin.
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

        // 3. Search Button
        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            // Explicit Intent: "I want to start SearchActivity class"
            val intent = android.content.Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // --- Navigation Drawer Logic ---

        // Toggle: Open drawer when menu icon is clicked
        findViewById<android.view.View>(R.id.imgMenu).setOnClickListener {
            if (!drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        // Populate Drawer Info: Read from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "email@example.com")
        val userPhone = sharedPreferences.getString("userPhone", "No Phone")

        findViewById<android.widget.TextView>(R.id.tvDrawerName).text = userName
        findViewById<android.widget.TextView>(R.id.tvDrawerEmail).text = userEmail
        findViewById<android.widget.TextView>(R.id.tvDrawerPhone).text = userPhone

        // Logout: Clear data and clean backstack
        findViewById<android.view.View>(R.id.btnLogout).setOnClickListener {
            sharedPreferences.edit().clear().apply()

            val intent = android.content.Intent(this, MainActivity::class.java)
            // FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK clears the entire history stack
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Navigation: Close drawer if "Home" is clicked (since we are already here)
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        // --- Category Logic ---
        
        // Setup click listeners for all categories
        setupCategory(R.id.imgGeneral, "General")
        setupCategory(R.id.imgBusiness, "Business")
        setupCategory(R.id.imgSports, "Sports")
        setupCategory(R.id.imgTechnology, "Technology")
        setupCategory(R.id.imgEntertainment, "Entertainment")
        setupCategory(R.id.imgHealth, "Health")
        setupCategory(R.id.imgScience, "Science")
    }

    /**
     * Helper function to reduce repeated code.
     * Takes an ID (for the ImageView) and a String (Category Name).
     */
    private fun setupCategory(viewId: Int, categoryName: String) {
        findViewById<android.view.View>(viewId).setOnClickListener {
            val intent = android.content.Intent(this, NewsActivity::class.java)
            // Passing Data: We put the category name into the Intent extras.
            // NewsActivity will retrieve this using key "CATEGORY_NAME".
            intent.putExtra("CATEGORY_NAME", categoryName)
            startActivity(intent)
        }
    }
}
