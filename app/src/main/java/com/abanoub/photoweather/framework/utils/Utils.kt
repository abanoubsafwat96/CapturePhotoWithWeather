package com.abanoub.photoweather.framework.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun isExternalStorageWritable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}