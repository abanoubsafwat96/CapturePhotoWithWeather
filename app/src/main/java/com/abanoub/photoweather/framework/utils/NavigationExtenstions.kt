package com.abanoub.photoweather.framework.utils

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.abanoub.photoweather.R

fun Activity.findNavController(): NavController? = try {
    this.findNavController(R.id.navHostFragment)
} catch (e: Exception) {
    Log.d("Tag", e.message.toString())
    null
}

fun NavController.navigateSafe(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun Fragment.popBackSafe(destinationId: Int, inclusive: Boolean) {
    try {
        findNavController().popBackStack(destinationId, inclusive)
    } catch (e: Exception) {
        Log.e("Tag", e.message.toString())
    }
}