package com.learning.app.core.logging

import android.util.Log
import com.learning.app.BuildConfig

object AppLogger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
            throwable?.printStackTrace()
        }
    }
}
