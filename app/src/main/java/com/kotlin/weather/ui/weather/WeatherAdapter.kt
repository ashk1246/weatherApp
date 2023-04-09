package com.kotlin.weather.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.example.Weather
import com.kotlin.weather.databinding.RowWeatherArticleBinding
import com.kotlin.weather.utils.load

/**
 * Created by Ashok
 */

/**
 * The News adapter to show the news in a list.
 */
class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.NewsHolder>() {

    /**
     * List of news articles
     */
    private var newsArticles: List<Weather> = emptyList()

    var onNewsClicked: ((Weather) -> Unit)? = null

    /**
     * Inflate the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val itemBinding =
            RowWeatherArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsHolder(itemBinding)
    }

    /**
     * Bind the view with the data
     */
    override fun onBindViewHolder(newsHolder: NewsHolder, position: Int) =
        newsHolder.bind(newsArticles[position])

    /**
     * Number of items in the list to display
     */
    override fun getItemCount() = newsArticles.size

    /**
     * View Holder Pattern
     */
    inner class NewsHolder(private val itemBinding: RowWeatherArticleBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(newsArticle: Weather) = with(itemView) {
            var url = "https://openweathermap.org/img/wn/${newsArticle.icon}@2x.png"
            itemBinding.ivWeatherImage.load(itemBinding.root.context, url)
            itemBinding.tvMain.text = "Weather : " + newsArticle.main
            itemBinding.tvDesc.text = "Description : " + newsArticle.description
            itemBinding.root.setOnClickListener {
                onNewsClicked?.invoke(newsArticle)
            }
        }
    }

    /**
     * Swap function to set new data on updating
     */
    @SuppressLint("NotifyDataSetChanged")
    fun replaceItems(items: List<Weather>) {
        newsArticles = items
        notifyDataSetChanged()
    }
}
