package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * MainActivity: The entry point of the application.
 *
 * This Activity handles functionality:
 * 1. App Startup: It is the first screen launched (configured in AndroidManifest.xml).
 * 2. Registration: Allows new users to enter their details.
 * 3. Auto-Login: Checks if the user is already saved in SharedPreferences.
 * 4. Navigation: Redirects to [HomeActivity] if logged in.
 */
class MainActivity : AppCompatActivity() {
    
    /**
     * onCreate calls when the Activity is first created.
     * This is where we initialize the UI and background logic.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Enable Edge-to-Edge display (allows content to draw behind status/navigation bars)
        enableEdgeToEdge()
        
        // 2. Initialize SharedPreferences for Data Persistence
        // "UserPrefs" is the file name, MODE_PRIVATE means only this app can access it.
        // We use this to save small amounts of data (like login status) permanently.
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 3. Auto-Login Check
        if (isLoggedIn) {
            // Logic: If user is logged in, skip registration and go directly to HomeActivity.
            val intent = android.content.Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Important: Closes MainActivity so 'Back' button doesn't return here.
            return // Stop further execution of onCreate.
        }

        // Set the layout file (XML) for this activity
        setContentView(R.layout.activity_main)

        // 4. Handle Window Insets (Padding for System Bars)
        // This ensures our UI elements (buttons, text) don't get hidden behind the status bar or notch.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Converting 24dp (density-independent pixels) to specific pixels for the device screen
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

        // 5. Setup UI References using findViewById
        val btnRegister = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegister)
        val etName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
        val etEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone)
        
        // 6. Register Button Click Logic
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()

            // Basic validation: Ensure fields are not empty
            if (name.isNotEmpty() && email.isNotEmpty()) {
                // Save data to SharedPreferences (Persistence)
                val editor = sharedPreferences.edit()
                editor.putString("userName", name)
                editor.putString("userEmail", email)
                editor.putString("userPhone", phone)
                editor.putBoolean("isLoggedIn", true) // Set login flag to true
                editor.apply() // Commit changes asynchronously (unlike commit() which is synchronous)

                // Navigate to Dashboard (HomeActivity)
                val intent = android.content.Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Close this activity
            }
        }
    }
}