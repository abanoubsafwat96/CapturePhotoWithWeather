package com.abanoub.photoweather.framework.utils.permissions

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {
    // Individual permissions
    object Camera : Permission(CAMERA)

    // Grouped permissions
    object Storage : Permission(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    object Location : Permission(ACCESS_COARSE_LOCATION)

    companion object {
        fun from(permission: String) = when (permission) {
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            ACCESS_COARSE_LOCATION -> Location
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
