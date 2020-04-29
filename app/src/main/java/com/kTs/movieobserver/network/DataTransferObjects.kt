package com.kTs.movieobserver.network

import com.kTs.movieobserver.database.DatabaseMovie
import com.kTs.movieobserver.domain.Movie
import com.kTs.movieobserver.utils.NullToEmptyString
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ContainerType {
    NOW_PLAYING,
    UPCOMING
}

@JsonClass(generateAdapter = true)
data class MoviesContainer(@Json(name = "results") val movies: List<NetworkMovie>)

@JsonClass(generateAdapter = true)
data class NetworkMovie(
    @NullToEmptyString val popularity: String?,
    @Json(name = "vote_count") @NullToEmptyString val voteCount: String?,
    val video: Boolean?,
    @Json(name = "poster_path") @NullToEmptyString val posterPath: String?,
    val id: Int,
    val adult: Boolean?,
    @Json(name = "backdrop_path") @NullToEmptyString val backdropPath: String?,
    @Json(name = "original_language") @NullToEmptyString val originalLanguage: String?,
    @Json(name = "original_title") @NullToEmptyString val originalTitle: String?,
    @Transient
    val genreIds: List<String>? = null,
    @NullToEmptyString val title: String?,
    @Json(name = "vote_average") @NullToEmptyString val voteAverage: String?,
    @NullToEmptyString val overview: String?,
    @Json(name = "release_date") @NullToEmptyString val releaseDate: String?
)

fun MoviesContainer.asDomainModel(containerType: ContainerType): List<Movie> {
    return movies.map {
        Movie(
            popularity = it.popularity ?: "",
            voteCount = it.voteCount ?: "",
            video = it.video ?: false,
            posterPath = it.posterPath ?: "",
            id = it.id,
            adult = it.adult ?: false,
            backdropPath = it.backdropPath ?: "",
            originalLanguage = it.originalLanguage ?: "",
            originalTitle = it.originalTitle ?: "",
            title = it.originalTitle ?: "",
            voteAverage = it.voteAverage ?: "",
            overview = it.overview ?: "",
            releaseDate = it.releaseDate ?: "",
            upcoming = containerType == ContainerType.UPCOMING,
            nowPlaying = containerType == ContainerType.NOW_PLAYING,
            isFavorite = false
        )
    }
}

fun MoviesContainer.asDatabaseModel(containerType: ContainerType): List<DatabaseMovie> {
    return movies.map {
        DatabaseMovie(
            popularity = it.popularity ?: "",
            voteCount = it.voteCount ?: "",
            video = it.video ?: false,
            posterPath = it.posterPath ?: "",
            id = it.id,
            adult = it.adult ?: false,
            backdropPath = it.backdropPath ?: "",
            originalLanguage = it.originalLanguage ?: "",
            originalTitle = it.originalTitle ?: "",
            title = it.originalTitle ?: "",
            voteAverage = it.voteAverage ?: "",
            overview = it.overview ?: "",
            releaseDate = it.releaseDate ?: "",
            upcoming = containerType == ContainerType.UPCOMING,
            nowPlaying = containerType == ContainerType.NOW_PLAYING,
            isFavorite = false
        )
    }
}