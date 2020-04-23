package com.listapplication.utils

import android.content.SharedPreferences

class PreferencesUtil(
    private val mPrefs: SharedPreferences
) {

    fun saveResponseTimeout(responseTimeout: Long) {
        mPrefs.edit().putLong(RESPONSE_TIMEOUT, responseTimeout).apply()
    }

    fun getResponseTimeout(): Long {
        return mPrefs.getLong(RESPONSE_TIMEOUT, 0)
    }

    companion object {
        const val RESPONSE_TIMEOUT = "response_timeout"
    }
}