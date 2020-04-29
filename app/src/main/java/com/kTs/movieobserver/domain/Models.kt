package com.kTs.movieobserver.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val popularity: String,
    val voteCount: String,
    val video: Boolean,
    val posterPath: String,
    val id: Int,
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
) : Parcelable