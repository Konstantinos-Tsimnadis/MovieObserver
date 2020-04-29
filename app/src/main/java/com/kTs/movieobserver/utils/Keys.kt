package com.kTs.movieobserver.utils

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun tmdbApiKey(): String
}