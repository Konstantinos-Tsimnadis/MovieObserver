package com.kTs.movieobserver.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kTs.movieobserver.domain.Movie

@Entity
data class DatabaseMovie constructor(
    @PrimaryKey
    val id: Int,
    val popularity: String,
    val voteCount: String,
    val video: Boolean,
    val posterPath: String,
    val adult: Boolean,
    val backdropPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val title: String,
    val voteAverage: String,
    val overview: String,
    val releaseDate: String,
    val upcoming: Boolean,
    val nowPlaying: Boolean,
    val isFavorite: Boolean
)

fun List<DatabaseMovie>.asDomainModel(): List<Movie> {
    return map {
        Movie(
            popularity = it.popularity,
            voteCount = it.voteCount,
            video = it.video,
            posterPath = it.posterPath,
            id = it.id,
            adult = it.adult,
            backdropPath = it.backdropPath,
            originalLanguage = it.originalLanguage,
            originalTitle = it.originalTitle,
            title = it.originalTitle,
            voteAverage = it.voteAverage,
            overview = it.overview,
            releaseDate = it.releaseDate,
            upcoming = it.upcoming,
            nowPlaying = it.nowPlaying,
            isFavorite = it.isFavorite
        )
    }
}