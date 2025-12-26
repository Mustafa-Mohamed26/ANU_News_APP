package com.example.anu_news_app

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

/**
 * WebViewActivity: Hosts a [WebView] to display the full news article from a URL.
 */
class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // Retrieve the URL passed from NewsActivity
        val url = intent.getStringExtra("EXTRA_URL")
        val webView = findViewById<WebView>(R.id.webView)
        val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            finish() // Go back when toolbar back arrow is clicked
        }
        
        // --- WebView Setup ---
        // Enable JavaScript (Modern pages require it)
        webView.settings.javaScriptEnabled = true
        // Enable local storage (cookies/storage)
        webView.settings.domStorageEnabled = true
        // Automatically load images
        webView.settings.loadsImagesAutomatically = true
        // Enable pinch-to-zoom
        webView.settings.setSupportZoom(true)
        
        // WebViewClient: Handles navigation events within the WebView (e.g., loading a new URL).
        // By default, clicking a link might open Chrome. This keeps it in the app.
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // When page finishes loading, hide the progress bar
                if (progressBar.visibility == android.view.View.VISIBLE) {
                    progressBar.visibility = android.view.View.GONE
                }
            }
        }

        // WebChromeClient: Handles browser-level events like progress updates, alerts, etc.
        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Update progress bar as page loads
                if (newProgress == 100) {
                    progressBar.visibility = android.view.View.GONE
                } else {
                    progressBar.visibility = android.view.View.VISIBLE
                    progressBar.progress = newProgress
                }
            }
        }

        // Use loadUrl to start navigation
        if (!url.isNullOrEmpty()) {
            webView.loadUrl(url)
        }
    }
}
