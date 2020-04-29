package com.kTs.movieobserver.ui.favoriteMoviesScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.FavoriteMoviesFragmentBinding
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.utils.dpToPx
import com.kTs.movieobserver.utils.GridSpacingItemDecoration
import com.google.android.material.snackbar.Snackbar

class FavoriteMoviesFragment : Fragment() {

    private val viewModel: FavoriteMoviesViewModel by lazy {
        val activity = requireNotNull(this.activity)
        val vMFacrory = FavoriteMoviesViewModel.Factory(activity.application)
        ViewModelProvider(this, vMFacrory).get(FavoriteMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FavoriteMoviesFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.favorite_movies_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel

        val favoritesGridAdapter =
            FavoritesGridAdapter(FavoritesGridAdapter.OnClickListener { view: View, movie: Movie ->
                viewModel.navigationToDetails(movie)
            })
        binding.favoritesRecyclerview.adapter = favoritesGridAdapter
        binding.favoritesRecyclerview.addItemDecoration(GridSpacingItemDecoration(2,14.dpToPx,true))

        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { movie ->
            movie?.let {
                findNavController().navigate(
                    FavoriteMoviesFragmentDirections.actionFavoriteMoviesFragmentToMovieDetailFragment(
                        it
                    )
                )
                viewModel.navigationToDetailsComplete()
            }
        })

        viewModel.navigateToPresenter.observe(viewLifecycleOwner, Observer { navigation ->
            if (navigation) {
                findNavController().navigate(FavoriteMoviesFragmentDirections.actionFavoriteMoviesFragmentToNowPlayingMoviesFragment())
                viewModel.navigationToPresenterComplete()
            }
        })

        viewModel.undoSnackbarEvent.observe(viewLifecycleOwner, Observer { showSnackbar ->
            if (showSnackbar) {
                val snackbar =
                    Snackbar.make(
                        binding.root,
                        "Movie deleted from favorites.",
                        Snackbar.LENGTH_LONG
                    )
                snackbar.setAction("Undo") { viewModel.undoDeleteMovie() }
                snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.snackbar_action_text_color))
                snackbar.show()
                viewModel.showUndoSnackbarComplete()
            }
        })

        viewModel.storedMovies.observe(viewLifecycleOwner, Observer { movieList ->
            favoritesGridAdapter.submitList(movieList)
        })

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("not implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val swipedMovieId = favoritesGridAdapter.getMovieAt(viewHolder.layoutPosition).id
                viewModel.setFavorite(swipedMovieId, false)
                viewModel.setRecentDeletedMovie(swipedMovieId)
                viewModel.showUndoSnackbar()
            }
        }).attachToRecyclerView(binding.favoritesRecyclerview)

        return binding.root
    }
}
