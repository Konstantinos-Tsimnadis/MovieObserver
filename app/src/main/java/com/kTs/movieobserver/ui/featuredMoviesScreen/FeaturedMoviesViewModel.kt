package com.kTs.movieobserver.ui.featuredMoviesScreen

import android.accounts.NetworkErrorException
import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.kTs.movieobserver.R
import com.kTs.movieobserver.database.getDatabase
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.repository.MovieRepository
import com.kTs.movieobserver.utils.*
import kotlinx.coroutines.*
import java.net.UnknownHostException


class FeaturedMoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val repository = MovieRepository(database)

    private var _presentedMoviesType = MutableLiveData<String>()
    val presentedMoviesType: LiveData<String>
        get() = _presentedMoviesType

    val presentedMovies: LiveData<List<Movie>> = Transformations.switchMap(_presentedMoviesType) {
        choosePresentedMovies()
    }

    val connectionStateLiveData: LiveData<NetworkAvailability> =
        ConnectionStateLiveData.get(application)

    private var _networkStatus = MutableLiveData<NetworkStatus>()
    val networkStatus: MutableLiveData<NetworkStatus>
        get() = _networkStatus

    private var _navigateToDetails = MutableLiveData<Movie>()
    val navigateToDetails: LiveData<Movie>
        get() = _navigateToDetails

    private var _favoriteSnackbarEvent = MutableLiveData<Boolean>()
    val favoriteSnackbarEvent: LiveData<Boolean>
        get() = _favoriteSnackbarEvent

    private var _firstLaunch = MutableLiveData<Boolean>()
    val firstLaunch: LiveData<Boolean>
        get() = _firstLaunch

    init {
        _firstLaunch.value =
            SharedPreferencesHelper.getBooleanPreference(SharedPreferencesHelper.FIRST_LAUNCH, true)
        refreshDataFromRepository()
        _presentedMoviesType.value = getPresentedMoviesType()
        _favoriteSnackbarEvent.value = null
        _networkStatus.value = NetworkStatus.LOADING
    }

    private fun getPresentedMoviesType(): String {
        return SharedPreferencesHelper.getStringPreference(
            SharedPreferencesHelper.SETTING_STARTING_CONTENT,
            StartingScreenMovies.UNKNOWN.stringValue
        )
    }

    fun getSnackbarText(value: Boolean): String {
        return if (value) {
            getApplication<Application>().resources.getString(R.string.snackbar_movie_added)
        } else {
            getApplication<Application>().resources.getString(
                R.string.snackbar_movie_removed
            )
        }
    }

    fun showFavoriteSnackbarComplete() {
        _favoriteSnackbarEvent.value = null
    }

    fun navigationToDetailsDone() {
        _navigateToDetails.value = null
    }

    private fun navigationToDetails(movie: Movie) {
        _navigateToDetails.value = movie
    }

    fun setPresentType(value: String) {
        _presentedMoviesType.value = value
    }

    private fun setFavorite(id: Int, value: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, value)
        }
        _favoriteSnackbarEvent.value = value
    }

    fun movieClickHandler(view: View, movie: Movie) {
        when (view.id) {
            R.id.featured_item_favorite_button -> if (movie.isFavorite) {
                setFavorite(movie.id, false)
            } else {
                setFavorite(movie.id, true)
            }

            R.id.featured_movie_container -> navigationToDetails(movie)
        }
    }

    private fun choosePresentedMovies(): LiveData<List<Movie>> {
        val adultSetting =
            SharedPreferencesHelper.getBooleanPreference(
                SharedPreferencesHelper.SETTING_ADULT,
                false
            )
        return if (_presentedMoviesType.value == StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue) {
            if (adultSetting) {
                repository.moviesListCurrentAdultEnabled
            } else {
                repository.moviesListCurrentAdultDisabled
            }
        } else {
            if (adultSetting) {
                repository.moviesListUpcomingAdultEnabled
            } else {
                repository.moviesListUpcomingAdultDisabled
            }
        }
    }

    private fun firstLaunchCompleted() {
        if (_firstLaunch.value == true) {
            _firstLaunch.value = false
            SharedPreferencesHelper.setBooleanPreference(
                SharedPreferencesHelper.FIRST_LAUNCH,
                false
            )
            setPresentedMoviesTypeFirstLaunch()
        }
    }

    private fun setPresentedMoviesTypeFirstLaunch() {
        if (getPresentedMoviesType() == StartingScreenMovies.UNKNOWN.stringValue) {
            SharedPreferencesHelper.setStringPreference(
                SharedPreferencesHelper.SETTING_STARTING_CONTENT,
                StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue
            )
            _presentedMoviesType.value = StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue
        }
    }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                _networkStatus.value = NetworkStatus.LOADING
                delay(3000)
                repository.refreshMovies()
                firstLaunchCompleted()

                _networkStatus.value = NetworkStatus.SUCCESS
            } catch (e: NetworkErrorException) {
                _networkStatus.value = NetworkStatus.ERROR
            } catch (e: UnknownHostException) {
                _networkStatus.value = NetworkStatus.ERROR
            }
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeaturedMoviesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FeaturedMoviesViewModel(app) as T
            } else {
                throw(IllegalArgumentException("ViewModel issue"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
