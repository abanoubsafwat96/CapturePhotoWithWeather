package com.abanoub.photoweather.framework.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackBar(msg: String) {
    Snackbar.make(this.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
}

fun Fragment.openAppPermissionsSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity?.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Double.convertToCelsius() = this - 273.15