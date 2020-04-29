package com.kTs.movieobserver

import android.app.Application
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kTs.movieobserver.utils.SharedPreferencesHelper
import com.kTs.movieobserver.utils.handleDarkMode
import com.kTs.movieobserver.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MovieAppApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesHelper.init(applicationContext)
        handleTheme()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun handleTheme() {
        val isDarkModeEnabled = SharedPreferencesHelper.getBooleanPreference(
            SharedPreferencesHelper.SETTING_DARK_MODE,
            false
        )
        handleDarkMode(isDarkModeEnabled)
    }

    private fun setupRecurringWork() {

        val workConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS).setConstraints(
                workConstraints
            ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.REFRESH_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}