package com.kTs.movieobserver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kTs.movieobserver.database.MovieDatabase
import com.kTs.movieobserver.database.asDomainModel
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.network.ContainerType
import com.kTs.movieobserver.network.MoviesNetwork
import com.kTs.movieobserver.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(private val database: MovieDatabase) {

    val moviesListCurrentAdultEnabled: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getAllNowPlayingAdultEnabled()) { list ->
            list.asDomainModel()
        }

    val moviesListUpcomingAdultEnabled: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getAllUpcomingAdultEnabled()) { list ->
            list.asDomainModel()
        }

    val moviesListCurrentAdultDisabled: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getAllNowPlayingAdultDisabled()) { list ->
            list.asDomainModel()
        }

    val moviesListUpcomingAdultDisabled: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getAllUpcomingAdultDisabled()) { list ->
            list.asDomainModel()
        }

    val moviesListFavorites: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getAllFavorites()) { list ->
            list.asDomainModel()
        }

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val moviesUpcoming = MoviesNetwork.movies.getMoviesUpcoming().await()
            val moviesCurrent = MoviesNetwork.movies.getMoviesCurrent().await()
            database.movieDao.insertAll(moviesUpcoming.asDatabaseModel(ContainerType.UPCOMING))
            database.movieDao.insertAll(moviesCurrent.asDatabaseModel(ContainerType.NOW_PLAYING))
        }
    }

    suspend fun updateFavorite(id: Int, value: Boolean) {
        withContext(Dispatchers.IO) {
            database.movieDao.updateFavorite(id, value)
        }
    }
}