package com.example.learningpandaai.core.util

import android.util.Log
import com.example.learningpandaai.BuildConfig

object Logger {
    const val TAG = "LearningPanda"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun w(message: String) {
        if (BuildConfig.DEBUG) Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(TAG, message, throwable)
    }
}