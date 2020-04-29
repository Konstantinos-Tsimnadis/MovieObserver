package com.kTs.movieobserver.ui.featuredMoviesScreen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.FeaturedMoviesFragmentBinding
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.utils.ConnectionStateLiveData
import com.kTs.movieobserver.utils.NetworkAvailability
import com.kTs.movieobserver.utils.StartingScreenMovies
import com.google.android.material.snackbar.Snackbar

class FeaturedMoviesFragment : Fragment() {

    private val viewModel: FeaturedMoviesViewModel by lazy {
        val activity = requireNotNull(this.activity)
        val vMFactory = FeaturedMoviesViewModel.Factory(activity.application)
        ViewModelProvider(this, vMFactory).get(FeaturedMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FeaturedMoviesFragmentBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.featured_movies_fragment,
                container,
                false
            )

        setHasOptionsMenu(true)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val presenterListAdapter =
            MoviesAdapter(MoviesAdapter.OnClickListener { view: View, movie: Movie ->
                viewModel.movieClickHandler(view, movie)
            })

        binding.presenterRecyclerView.adapter = presenterListAdapter

        viewModel.favoriteSnackbarEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                Snackbar.make(
                    binding.root,
                    viewModel.getSnackbarText(it),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.showFavoriteSnackbarComplete()
            }
        })

        viewModel.presentedMovies.observe(viewLifecycleOwner, Observer {
            presenterListAdapter.addHeaderAndSubmitList(
                viewModel.presentedMoviesType.value
                    ?: StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue, it
            )
        })

        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { movie ->
            movie?.let {
                findNavController().navigate(
                    FeaturedMoviesFragmentDirections.actionNowPlayingMoviesFragmentToMovieDetailFragment(
                        it
                    )
                )
                viewModel.navigationToDetailsDone()
            }
        })

        context?.let { context ->
            ConnectionStateLiveData.get(context)
                .observe(viewLifecycleOwner, Observer { internetAvailable ->
                    if (internetAvailable == NetworkAvailability.CONNECTED) {
                        viewModel.refreshDataFromRepository()
                    }
                })
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.featured_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_menu_new -> viewModel.setPresentType(StartingScreenMovies.UPCOMING_MOVIES.stringValue)
            R.id.toolbar_menu_now -> viewModel.setPresentType(StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue)
        }
        return true
    }
}
