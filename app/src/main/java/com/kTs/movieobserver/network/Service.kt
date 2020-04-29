package com.kTs.movieobserver.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kTs.movieobserver.utils.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/movie/"
private val API_KEY = Keys.tmdbApiKey()
private val LANGUAGE = LocaleHelper().getLocaleStringFromLanguage(
    SharedPreferencesHelper.getStringPreference(
        SharedPreferencesHelper.SETTING_LANGUAGE,
        Languages.ENGLISH.stringValue
    )
)

interface MovieService {

    @GET("now_playing")
    fun getMoviesCurrent(@Query("api_key") apiKey: String = API_KEY, @Query("language") language: String = LANGUAGE): Deferred<MoviesContainer>

    @GET("upcoming")
    fun getMoviesUpcoming(@Query("api_key") apiKey: String = API_KEY, @Query("language") language: String = LANGUAGE): Deferred<MoviesContainer>
}

object MoviesNetwork {
    private val moshi = Moshi.Builder()
        .add(NullToEmptyStringAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val movies = retrofit.create(MovieService::class.java)
}


