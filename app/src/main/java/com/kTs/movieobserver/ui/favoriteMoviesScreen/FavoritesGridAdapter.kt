package com.kTs.movieobserver.ui.favoriteMoviesScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.FavoriteGridItemBinding
import com.kTs.movieobserver.domain.Movie

class FavoritesGridAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Movie, FavoritesGridAdapter.FavoritesViewHolder>(FavoritesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val movie = getItem(position)
        holder.binding.gridItemContainer.setOnClickListener {
            onClickListener.onClick(it, movie)
        }
        holder.bind(movie)
    }

    class FavoritesViewHolder(val binding: FavoriteGridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavoritesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: FavoriteGridItemBinding =
                    DataBindingUtil.inflate(inflater, R.layout.favorite_grid_item, parent, false)
                return FavoritesViewHolder(binding)
            }
        }
    }

    fun getMovieAt(position: Int): Movie {
        return getItem(position)
    }

    class FavoritesDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (view: View, movie: Movie) -> Unit) {
        fun onClick(view: View, movie: Movie) = clickListener(view, movie)
    }
}