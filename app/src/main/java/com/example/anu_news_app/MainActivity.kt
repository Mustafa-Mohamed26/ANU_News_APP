package com.example.anu_news_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 1. Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 2. Check if already logged in -> Navigate to HomeActivity
        if (isLoggedIn) {
            val intent = android.content.Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Prevents going back to MainActivity
            return // Stop further execution of MainActivity onCreate
        }

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Convert 24dp to pixels
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

        // 3. Register Button Logic
        val btnRegister = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegister)
        val etName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
        val etEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone)
        
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("userName", name)
                editor.putString("userEmail", email)
                editor.putString("userPhone", phone)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                val intent = android.content.Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}