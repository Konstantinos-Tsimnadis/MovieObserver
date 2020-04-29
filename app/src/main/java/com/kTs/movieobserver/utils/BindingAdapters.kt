package com.kTs.movieobserver.utils

import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kTs.movieobserver.R
import com.kTs.movieobserver.domain.Movie

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, url: String?) {
    url?.let {
        val uri = getFullImageUrl(it).toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
            )
            .into(imageView)
    }
}

@BindingAdapter("setEntries")
fun setEntries(spinner: Spinner, entries: List<Any>?) {
    if (entries != null) {
        val arrayAdapter =
            ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, entries)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }
}

@BindingAdapter("onSelected")
fun setOnSelectedListener(spinner: Spinner, listener: ItemSelectedListener?) {
    if (listener == null) {
        spinner.onItemSelectedListener = null
    } else {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (spinner.tag != position) {
                    listener.onItemSelected(parent?.getItemAtPosition(position))
                }
            }
        }
    }
}

@BindingAdapter("setValue")
fun setValue(spinner: Spinner, newValue: Any?) {
    if (spinner.adapter != null) {
        val storedLanguage = SharedPreferencesHelper.getStringPreference(
            SharedPreferencesHelper.SETTING_LANGUAGE,
            Languages.ENGLISH.stringValue
        )
        if (storedLanguage == Languages.GERMAN.stringValue) {
            spinner.setSelection(1, false)
            spinner.tag = 1
        } else {
            spinner.setSelection(0, false)
            spinner.tag = 0
        }
    }
}

interface ItemSelectedListener {
    fun onItemSelected(item: Any?)
}

@BindingAdapter("imageStatus", "movieList")
fun bindNetworkStatus(imageView: ImageView, status: NetworkStatus, movieList: List<Movie>?) {
    if (movieList.isNullOrEmpty()) {
        when (status) {
            NetworkStatus.LOADING -> {
                imageView.setImageResource(R.drawable.loading_animation)
                imageView.layoutParams.height = 150.dpToPx
                imageView.layoutParams.width = 150.dpToPx
                imageView.scaleType = ImageView.ScaleType.FIT_XY
            }
            NetworkStatus.ERROR -> {
                imageView.setImageResource(R.drawable.ic_connection_error)
                imageView.layoutParams.height = 70.dpToPx
                imageView.layoutParams.width = 70.dpToPx
            }
            NetworkStatus.SUCCESS -> imageView.visibility = View.GONE
        }
    } else {
        imageView.visibility = View.GONE
    }
}

@BindingAdapter("networkStatus", "firstLaunch")
fun noInternetTextVisibility(
    textView: TextView,
    status: NetworkAvailability,
    firstLaunch: Boolean
) {
    if (firstLaunch) {
        when (status) {
            NetworkAvailability.UNKNOWN -> textView.visibility = View.VISIBLE
            NetworkAvailability.CONNECTED -> textView.visibility = View.GONE
            NetworkAvailability.DISCONNECTED -> textView.visibility = View.VISIBLE
        }
    } else {
        textView.visibility = View.GONE
    }
}
