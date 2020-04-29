package com.kTs.movieobserver.ui.movieDetailScreen

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.MovieDetailFragmentBinding
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.utils.getLocalBitmapUri


class MovieDetailFragment : Fragment() {

    private lateinit var binding: MovieDetailFragmentBinding
    private lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.movie_detail_fragment, container, false)

        setHasOptionsMenu(true)
        movie = MovieDetailFragmentArgs.fromBundle(requireArguments()).movie

        binding.movie = movie
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> shareMovie()
            android.R.id.home -> {
                val navController = this.findNavController()
                return navController.navigateUp()
            }
        }
        return true
    }

    private fun shareMovie() {
        val imageUri = getLocalBitmapUri(requireContext(), binding.movieDetailImage)
        imageUri?.let {
            ShareCompat.IntentBuilder
                .from(activity as Activity)
                .setType("image/*")
                .setChooserTitle(resources.getString(R.string.share_movie))
                .setText(resources.getString(R.string.detail_share_text))
                .addStream(imageUri)
                .startChooser()
        }
    }

}
