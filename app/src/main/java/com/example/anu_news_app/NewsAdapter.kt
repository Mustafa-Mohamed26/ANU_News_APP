package com.example.anu_news_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for the RecyclerView in NewsActivity.
 * This class binds the data (NewsItem) to the views defined in item_news.xml.
 *
 * @param newsList The list of news items to display.
 * @param onItemClicked A function (lambda) that will be called when a user clicks on an item.
 */

class NewsAdapter(
    private var articles: List<com.example.anu_news_app.model.Article>,
    private val onItemClicked: (com.example.anu_news_app.model.Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgNews: ImageView = itemView.findViewById(R.id.imgNews)
        val tvNewsTitle: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val tvNewsAuthor: TextView = itemView.findViewById(R.id.tvNewsAuthor)
        val tvNewsTime: TextView = itemView.findViewById(R.id.tvNewsTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.tvNewsTitle.text = article.title
        holder.tvNewsAuthor.text = article.author ?: "Unknown Author"
        holder.tvNewsTime.text = article.publishedAt ?: ""
        
        com.bumptech.glide.Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .placeholder(R.drawable.ic_news_placeholder)
            .error(R.drawable.ic_news_placeholder)
            .into(holder.imgNews)

        holder.itemView.setOnClickListener {
            onItemClicked(article)
        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(newList: List<com.example.anu_news_app.model.Article>) {
        articles = newList
        notifyDataSetChanged()
    }
}
