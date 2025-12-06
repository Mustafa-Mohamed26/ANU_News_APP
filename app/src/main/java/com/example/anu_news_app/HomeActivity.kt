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

        findViewById<android.view.View>(R.id.imgSearch).setOnClickListener {
            val intent = android.content.Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}
