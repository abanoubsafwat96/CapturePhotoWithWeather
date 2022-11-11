package com.abanoub.photoweather.framework.utils.permissions

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {
    object AppPermissions :
        Permission(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION)

    companion object {
        fun from(permission: String) = when (permission) {
            CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION -> AppPermissions
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
