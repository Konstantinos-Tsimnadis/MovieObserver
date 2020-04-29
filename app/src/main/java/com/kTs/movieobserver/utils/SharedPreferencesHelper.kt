package com.kTs.movieobserver.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    private lateinit var sharedPreferences: SharedPreferences

    private const val PREFERENCES_NAME = "movie_app_shared_preferences"

    const val FIRST_LAUNCH = "first_launch"
    const val SETTING_ADULT = "setting_adult"
    const val SETTING_DARK_MODE = "setting_dark_mode"
    const val SETTING_STARTING_CONTENT = "setting_starting_content"
    const val SETTING_LANGUAGE = "setting_language"


    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun getBooleanPreference(preference: String, default: Boolean): Boolean {
        return sharedPreferences.getBoolean(preference, default)
    }

    fun setBooleanPreference(preference: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(preference, value)
            commit()
        }
    }

    fun getStringPreference(preference: String, default: String): String {
        return sharedPreferences.getString(preference, default).toString()
    }

    fun setStringPreference(preference: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(preference, value)
            commit()
        }
    }
}