package com.abanoub.photoweather.framework.utils

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackBar(msg: String) {
    Snackbar.make(this.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
}