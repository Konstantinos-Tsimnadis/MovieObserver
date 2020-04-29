package com.kTs.movieobserver.ui.favoriteMoviesScreen

import android.app.Application
import androidx.lifecycle.*
import com.kTs.movieobserver.database.getDatabase
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.repository.MovieRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class FavoriteMoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val database = getDatabase(application)
    private val repository = MovieRepository(database)

    val storedMovies: LiveData<List<Movie>> = repository.moviesListFavorites

    private var _navigateToDetails = MutableLiveData<Movie>()
    val navigateToDetails: LiveData<Movie>
        get() = _navigateToDetails

    private var _undoSnackbarEvent = MutableLiveData<Boolean>()
    val undoSnackbarEvent: LiveData<Boolean>
        get() = _undoSnackbarEvent

    private var recentDeletedMovie = MutableLiveData<Int>()

    private var _navigateToPresenter = MutableLiveData<Boolean>()
    val navigateToPresenter: LiveData<Boolean>
        get() = _navigateToPresenter

    init {
        recentDeletedMovie.value = null
        _undoSnackbarEvent.value = false
        _navigateToPresenter.value = false
    }

    fun navigationToPresenter(){
        _navigateToPresenter.value = true
    }

    fun navigationToPresenterComplete(){
        _navigateToPresenter.value = false
    }

    fun setRecentDeletedMovie(movieId: Int) {
        recentDeletedMovie.value = movieId
    }

    fun showUndoSnackbarComplete() {
        _undoSnackbarEvent.value = false
    }

    fun showUndoSnackbar() {
        _undoSnackbarEvent.value = true
    }

    fun undoDeleteMovie() {
        recentDeletedMovie.value?.let {
            setFavorite(it, true)
            recentDeletedMovie.value = null
        }
    }

    fun navigationToDetailsComplete() {
        _navigateToDetails.value = null
    }

    fun navigationToDetails(movie: Movie) {
        _navigateToDetails.value = movie
    }

    fun setFavorite(id: Int, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, value)
        }
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteMoviesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteMoviesViewModel(app) as T
            } else {
                throw (IllegalArgumentException("ViewModel issue"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
