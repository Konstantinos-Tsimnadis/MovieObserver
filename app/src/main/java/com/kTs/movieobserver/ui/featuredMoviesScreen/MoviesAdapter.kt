package com.kTs.movieobserver.ui.featuredMoviesScreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.FeaturedListItemBinding
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.utils.StartingScreenMovies
import com.kTs.movieobserver.utils.dpToPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val HEADER_ITEM = 0
private const val MOVIE_ITEM = 1

class MoviesAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<MoviesAdapter.AdapterItem, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_ITEM -> HeaderViewHolder.from(parent)
            MOVIE_ITEM -> MovieViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holderMovie: RecyclerView.ViewHolder, position: Int) {
        when (holderMovie) {
            is MovieViewHolder -> {
                val adapterItem = getItem(position) as AdapterItem.MovieItem
                addFirstCardMargin(holderMovie, position)
                holderMovie.binding.featuredItemFavoriteButton.setOnClickListener {
                    onClickListener.onClick(
                        holderMovie.binding.featuredItemFavoriteButton,
                        adapterItem.movie
                    )
                }
                holderMovie.binding.featuredMovieContainer.setOnClickListener {
                    onClickListener.onClick(
                        holderMovie.binding.featuredMovieContainer,
                        adapterItem.movie
                    )
                }
                holderMovie.bind(adapterItem.movie)
            }
            is HeaderViewHolder -> {
                val adapterItem = getItem(position) as AdapterItem.HeaderItem
                holderMovie.itemView.findViewById<TextView>(R.id.category_header).text =
                    when (adapterItem.type) {
                        StartingScreenMovies.UPCOMING_MOVIES.stringValue -> holderMovie.itemView.context.resources.getString(
                            R.string.featured_content_upcoming
                        )
                        StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue -> holderMovie.itemView.context.resources.getString(
                            R.string.featured_content_now_playing
                        )
                        else -> holderMovie.itemView.context.resources.getString(R.string.featured_content_now_playing)
                    }
                holderMovie.itemView.visibility = when (adapterItem.type) {
                    StartingScreenMovies.UNKNOWN.stringValue -> View.GONE
                    else -> View.VISIBLE
                }
            }
        }
    }

    class MovieViewHolder private constructor(val binding: FeaturedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MovieViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: FeaturedListItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.featured_list_item,
                        parent,
                        false
                    )
                return MovieViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.movie_category_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    private fun addFirstCardMargin(holderMovie: MovieViewHolder, position: Int) {
        if (position == 1) {
            val lp =
                holderMovie.binding.featuredItemCardContainer.layoutParams as ViewGroup.MarginLayoutParams
            lp.topMargin = 2.dpToPx
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<AdapterItem>() {

        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (view: View, movie: Movie) -> Unit) {
        fun onClick(view: View, movie: Movie) = clickListener(view, movie)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdapterItem.MovieItem -> MOVIE_ITEM
            is AdapterItem.HeaderItem -> HEADER_ITEM
        }
    }

    fun addHeaderAndSubmitList(headerType: String, movieList: List<Movie>) {
        adapterScope.launch {
            val items = when (movieList) {
                null -> listOf(AdapterItem.HeaderItem(headerType))
                else -> listOf(AdapterItem.HeaderItem(headerType)) + movieList.map {
                    AdapterItem.MovieItem(
                        it
                    )
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    sealed class AdapterItem {
        abstract val id: Int

        class MovieItem(val movie: Movie) : AdapterItem() {
            override val id = movie.id
        }

        class HeaderItem(val type: String) : AdapterItem() {
            override val id = Int.MIN_VALUE
        }
    }
}