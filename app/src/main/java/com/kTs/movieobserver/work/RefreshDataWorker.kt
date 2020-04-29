package com.kTs.movieobserver.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kTs.movieobserver.database.getDatabase
import com.kTs.movieobserver.repository.MovieRepository
import retrofit2.HttpException

class RefreshDataWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val REFRESH_WORKER = "com.example.movieapp.work.RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = MovieRepository(database)

        try {
            repository.refreshMovies()
        } catch (e: HttpException) {
            Result.retry()
        }

        return Result.success()
    }

}