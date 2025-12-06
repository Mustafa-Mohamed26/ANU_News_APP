package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * MainActivity: The starting point of the application (Entry Point).
 * 
 * Responsibilities:
 * 1. User Registration (Name, Email, Phone, Password).
 * 2. Checking if the user is already logged in (Auto-Login).
 * 3. Navigating to the Home screen on success.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Enable Edge-to-Edge display (allows content to draw behind status/navigation bars)
        enableEdgeToEdge()
        
        // 2. Initialize SharedPreferences for Data Persistence
        // "UserPrefs" is the file name, MODE_PRIVATE means only this app can access it.
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 3. Auto-Login Check
        if (isLoggedIn) {
            // Logic: If user is logged in, skip registration and go to Home.
            val intent = android.content.Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Important: Closes MainActivity so 'Back' button doesn't return here.
            return // Stop `onCreate` execution.
        }

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

        // 5. Setup UI References
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
                // Save data to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("userName", name)
                editor.putString("userEmail", email)
                editor.putString("userPhone", phone)
                editor.putBoolean("isLoggedIn", true) // Set login flag to true
                editor.apply() // Commit changes asynchronously

                // Navigate to Dashboard (HomeActivity)
                val intent = android.content.Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Close this activity
            }
        }
    }
}