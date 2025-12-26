package com.example.anu_news_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for the RecyclerView in NewsActivity.
 *
 * This class is responsible for taking a list of [com.example.anu_news_app.model.Article] objects
 * and binding them to the views defined in the `item_news.xml` layout.
 *
 * @param articles The initial list of news articles to display.
 * @param onItemClicked A lambda function that acts as a callback when a user clicks on an article.
 */
class NewsAdapter(
    // We use 'var' so we can update the list later with 'updateData'
    private var articles: List<com.example.anu_news_app.model.Article>,
    private val onItemClicked: (com.example.anu_news_app.model.Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    /**
     * ViewHolder pattern: Holds references to the views in the list item layout.
     * This avoids calling findViewById() repeatedly during scrolling, improving performance.
     */
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgNews: ImageView = itemView.findViewById(R.id.imgNews)
        val tvNewsTitle: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val tvNewsAuthor: TextView = itemView.findViewById(R.id.tvNewsAuthor)
        val tvNewsTime: TextView = itemView.findViewById(R.id.tvNewsTime)
    }

    /**
     * Called when the RecyclerView needs a new [NewsViewHolder] of the given type to represent an item.
     * This happens when the list is first loaded or when the user scrolls and new items need to be created.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        // Inflate (convert xml to java/kotlin object) the layout for a single row
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the itemView to reflect the item at the given position.
     */
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        
        // Bind text data
        holder.tvNewsTitle.text = article.title
        holder.tvNewsAuthor.text = article.author ?: "Unknown Author" // Handle nullable author
        holder.tvNewsTime.text = article.publishedAt ?: ""
        
        // Load image using Glide library
        com.bumptech.glide.Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_news_placeholder) // Show this while loading
            .error(R.drawable.ic_news_placeholder) // Show this if error occurs
            .into(holder.imgNews)

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener {
            onItemClicked(article)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = articles.size

    /**
     * Updates the data in the adapter and refreshes the RecyclerView.
     *
     * @param newList The new list of articles to display.
     */
    fun updateData(newList: List<com.example.anu_news_app.model.Article>) {
        articles = newList
        // notifyDataSetChanged() tells the RecyclerView that the entire data set has changed.
        // It triggers a full re-layout and re-bind. For large lists, DiffUtil is more efficient.
        notifyDataSetChanged()
    }
}
