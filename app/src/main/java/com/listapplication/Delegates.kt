package com.listapplication

import android.util.Log

typealias CustomLogs = (msg: String) -> Int

fun customLogs(
    msg: String
) = if (BuildConfig.DEBUG) Log.d("ListApp", msg) else 0