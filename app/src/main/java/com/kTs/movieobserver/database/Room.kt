package com.kTs.movieobserver.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(movies: List<DatabaseMovie>)

    @Query("SELECT * FROM databasemovie WHERE upcoming = 1")
    fun getAllUpcomingAdultEnabled(): LiveData<List<DatabaseMovie>>

    @Query("SELECT * FROM databasemovie WHERE nowPlaying = 1")
    fun getAllNowPlayingAdultEnabled(): LiveData<List<DatabaseMovie>>

    @Query("SELECT * FROM databasemovie WHERE nowPlaying = 1 AND adult = 0")
    fun getAllNowPlayingAdultDisabled(): LiveData<List<DatabaseMovie>>

    @Query("SELECT * FROM databasemovie WHERE upcoming = 1 AND adult = 0")
    fun getAllUpcomingAdultDisabled(): LiveData<List<DatabaseMovie>>

    @Query("SELECT * FROM databasemovie")
    fun getAllMovies(): LiveData<List<DatabaseMovie>>

    @Query("UPDATE databasemovie SET isFavorite = :value WHERE id = :id")
    fun updateFavorite(id: Int, value: Boolean)

    @Query("SELECT * FROM databasemovie WHERE isFavorite = 1")
    fun getAllFavorites(): LiveData<List<DatabaseMovie>>
}

@Database(entities = [DatabaseMovie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
}

private lateinit var INSTANCE: MovieDatabase

fun getDatabase(context: Context): MovieDatabase {
    synchronized(MovieDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                "movies"
            ).build()
        }
    }
    return INSTANCE
}