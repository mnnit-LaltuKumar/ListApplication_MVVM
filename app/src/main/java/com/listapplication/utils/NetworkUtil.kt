package com.listapplication.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    fun isNetworkAvailable(applicationContext: Context?): Boolean {
        val connectivityManager =
            applicationContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}