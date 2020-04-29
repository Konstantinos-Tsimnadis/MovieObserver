package com.kTs.movieobserver.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import java.util.*


class LocaleHelper() {
    lateinit var context: Context

    constructor(context: Context) : this() {
        this.context = context
    }

    private fun getLocaleFromLanguage(language: String): Locale {
        return if (language == Languages.GERMAN.stringValue) {
            Locale("de")
        } else {
            Locale("en")
        }
    }

    fun getLocaleStringFromLanguage(language: String): String {
        return if (language == Languages.GERMAN.stringValue) {
            "de"
        } else {
            "en"
        }
    }

    fun setLocale(localeString: String) {
        val locale = getLocaleFromLanguage(localeString)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        val displayMetrics: DisplayMetrics = resources.displayMetrics

        configuration.setLocale(locale)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            context.applicationContext
                .createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, displayMetrics)
        }
    }

    fun updateBaseContextLocale(): Context? {
        val language: String = SharedPreferencesHelper.getStringPreference(
            SharedPreferencesHelper.SETTING_LANGUAGE,
            Languages.ENGLISH.stringValue
        )
        val locale = getLocaleFromLanguage(language)
        Locale.setDefault(locale)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
        } else updateResourcesLocaleLegacy(context, locale)
    }

    private fun updateResourcesLocaleLegacy(
        context: Context,
        locale: Locale
    ): Context? {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}