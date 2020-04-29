package com.kTs.movieobserver.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun getFullImageUrl(url: String): String {
    return "https://image.tmdb.org/t/p/w185$url"
}

fun handleDarkMode(isEnabled: Boolean) {
    if (isEnabled) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

enum class StartingScreenMovies(val stringValue: String) {
    UPCOMING_MOVIES("Upcoming"),
    NOW_PLAYING_MOVIES("Now playing"),
    UNKNOWN("Unknown")
}

enum class Languages(val stringValue: String) {
    ENGLISH("English"),
    GERMAN("German")
}

enum class NetworkStatus {
    LOADING,
    ERROR,
    SUCCESS
}

fun getLocalBitmapUri(context: Context, imageView: ImageView): Uri? {
    val drawable: Drawable = imageView.drawable
    var bmp: Bitmap?
    bmp = if (drawable is BitmapDrawable) {
        (imageView.drawable as BitmapDrawable).bitmap
    } else {
        return null
    }
    var bmpUri: Uri? = null
    try {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "share_image_" + System.currentTimeMillis() + ".png"
        )
        val out = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.close()

        bmpUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bmpUri
}