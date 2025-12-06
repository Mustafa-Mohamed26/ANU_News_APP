package com.example.anu_news_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class NewsItem(
    val title: String,
    val author: String,
    val time: String,
    val imageResId: Int
)

class NewsAdapter(private var newsList: List<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

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
        val news = newsList[position]
        holder.tvNewsTitle.text = news.title
        holder.tvNewsAuthor.text = news.author
        holder.tvNewsTime.text = news.time
        holder.imgNews.setImageResource(news.imageResId)
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newList: List<NewsItem>) {
        newsList = newList
        notifyDataSetChanged()
    }
}
