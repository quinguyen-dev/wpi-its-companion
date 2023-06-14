package com.example.intro_companion.frontend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.intro_companion.backend.entities.Article
import com.example.intro_companion.databinding.ListItemArticleBinding
import java.util.UUID

class ArticleAdapter(
    private val articles: List<Article>,
    private val onArticleClicked: (url: String) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleHolder>() {

    /**
     * Represents a view holder for a single item in a RecyclerView list of articles.
     * @param binding The binding object for the list_item_article.xml layout file.
     */
    inner class ArticleHolder(private val binding: ListItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an [Article] object to the corresponding views in the layout.
         * @param article The article to bind.
         * @param onArticleClicked A lambda function that is called when the article link is clicked.
         * The function takes a string parameter [url] representing the URL of the article.
         */
        fun bind(article: Article, onArticleClicked: (url: String) -> Unit) {
            binding.articleLabel.text = article.title

            binding.root.setOnClickListener {
                onArticleClicked(article.url)
            }
        }
    }

    /**
     * Called by the [RecyclerView] to create a new [ArticleHolder] object for a new item in the list.
     * @param parent The [ViewGroup] into which the new [ArticleHolder] object will be added.
     * @param viewType The type of the new view.
     * @return A new [ArticleHolder] object that holds a view for the new item.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemArticleBinding.inflate(inflater, parent, false)

        return ArticleHolder(binding)
    }

    /**
     * Called by the [RecyclerView] to display the data at the specified [position] in the list.
     * Updates the [ArticleHolder] object at [position] with the data from the corresponding [Article] object
     * and the [onArticleClicked] lambda function.
     * @param holder The [ArticleHolder] object to update.
     * @param position The position of the item to display.
     */
    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = articles[position]
        holder.bind(article, onArticleClicked)
    }

    /**
     * Returns the total number of items in the data set held by the [Adapter].
     * @return The total number of items in the data set.
     */
    override fun getItemCount() = articles.size

}