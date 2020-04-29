package com.kTs.movieobserver.ui.settingsScreen

import android.app.Application
import androidx.lifecycle.*
import com.kTs.movieobserver.R
import com.kTs.movieobserver.utils.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    var adultSetting =
        SharedPreferencesHelper.getBooleanPreference(SharedPreferencesHelper.SETTING_ADULT, true)
    private set

    var darkModeSetting =
        SharedPreferencesHelper.getBooleanPreference(
            SharedPreferencesHelper.SETTING_DARK_MODE,
            false
        )
    private set

    val startingContentType: String = SharedPreferencesHelper.getStringPreference(
        SharedPreferencesHelper.SETTING_STARTING_CONTENT,
        StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue
    )

    val startingContentTypeList: List<String> =
        application.resources.getStringArray(R.array.starting_content).asList()

    val languageList: List<String> = application.resources.getStringArray(R.array.language).asList()

    var languageType: String = SharedPreferencesHelper.getStringPreference(
        SharedPreferencesHelper.SETTING_LANGUAGE,
        Languages.ENGLISH.stringValue
    )

    private var _recreateActivity = MutableLiveData<Boolean>()
    val recreateActivity: LiveData<Boolean>
        get() = _recreateActivity

    init {
        _recreateActivity.value = false
    }

    fun recreateActivityCompleted() {
        _recreateActivity.value = false
    }

    fun setLanguageSettingPreference(spinnerValue: String) {
        SharedPreferencesHelper.setStringPreference(
            SharedPreferencesHelper.SETTING_LANGUAGE,
            spinnerValue
        )
        LocaleHelper(getApplication()).setLocale(spinnerValue)
        languageType = spinnerValue
        _recreateActivity.value = true
    }

    fun setAdultSettingPreference(value: Boolean) {
        adultSetting = value
        SharedPreferencesHelper.setBooleanPreference(SharedPreferencesHelper.SETTING_ADULT, value)
    }

    fun setDarkModeSettingPreference(value: Boolean) {
        darkModeSetting = value
        SharedPreferencesHelper.setBooleanPreference(
            SharedPreferencesHelper.SETTING_DARK_MODE,
            value
        )
        handleDarkMode(value)
    }

    fun setStartingContentSettingPreference(spinnerValue: String) {
        if (spinnerValue == StartingScreenMovies.UPCOMING_MOVIES.stringValue) {
            SharedPreferencesHelper.setStringPreference(
                SharedPreferencesHelper.SETTING_STARTING_CONTENT,
                StartingScreenMovies.UPCOMING_MOVIES.stringValue
            )
        } else {
            SharedPreferencesHelper.setStringPreference(
                SharedPreferencesHelper.SETTING_STARTING_CONTENT,
                StartingScreenMovies.NOW_PLAYING_MOVIES.stringValue
            )
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(app) as T
            } else {
                throw (ArithmeticException("ViewModel issue"))
            }
        }
    }
}
