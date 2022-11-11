package com.abanoub.photoweather.framework.utils.permissions

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.abanoub.photoweather.R
import com.abanoub.photoweather.framework.utils.openAppPermissionsSettings
import java.lang.ref.WeakReference

class PermissionManager private constructor(private val fragment: WeakReference<Fragment>) {

    private val requiredPermissions = mutableListOf<Permission>()
    private var rationaleMessage: String? = null
    private var isGrantedCallback: () -> Unit = {}
    private var isGrantedDetailedCallback: (Map<Permission, Boolean>) -> Unit = {}

    private val requestPermissions =
        fragment.get()?.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    companion object {
        fun from(fragment: Fragment) = PermissionManager(WeakReference(fragment))
    }

    /**
     * Function to check a one or few bundled permissions under one sealed class.
     * @param permission to be requested
     * example: Permission.Camera = Camera
     * example: Permission.Storage = Read + Write
     */
    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    /**
     * Function to set rationale to permission request to describe for users why should them grant
     * the permissions to be displayed if they didn't grant permission
     * @param validationDialog to be displayed if user didn't grant permission
     * @param rationaleMessage to be displayed inside dialog if user didn't grant permission
     */
    fun rationale(rationaleMessage: String): PermissionManager {
        this.rationaleMessage = rationaleMessage
        return this
    }

    /**
     * Function to start permissions check and register a callback to be called only in success result
     */
    fun checkPermission(callback: () -> Unit) {
        this.isGrantedCallback = callback
        startPermissionsCheck()
    }

    /**
     * Function to start check all permissions without bundling them and register a callback to be
     * called in success & not success results
     */
    fun checkDetailedPermission(callback: (Map<Permission, Boolean>) -> Unit) {
        this.isGrantedDetailedCallback = callback
        startPermissionsCheck()
    }

    /**
     * Helper function to start permissions check by checking:
     * if all permissions are granted then send granted result by invoking callbacks and clean up
     * else it will request Permissions to be shown to users to allow or deny it and get result
     * if user make don't show again for permission, Permission Rationale will be displayed
     * with description which entered before to describe for users why should they grant the permissions
     */
    private fun startPermissionsCheck() {
        fragment.get()?.let { fragment ->
            when {
                areAllPermissionsGranted(fragment) -> sendPositiveResult()
                else -> requestPermissions()
            }
        }
    }

    /**
     * Function to display Rationale dialog with description which entered before to describe for users
     * why should they grant the permissions
     * it should be called when access is denied, the user has checked the Don't ask again.
     */
    private fun displayRationale(fragment: Fragment) {

        AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.permission_needed))
            .setMessage(
                rationaleMessage ?: fragment.getString(R.string.you_should_grant_permission_to_continue)
            )
            .setCancelable(false)
            .setPositiveButton(fragment.getString(R.string.open_settings)) { _, _ ->
                fragment.openAppPermissionsSettings()
            }
            .show()
    }

    /**
     * Function to send granted result for requested permissions.
     * it should be call if all Permissions are Granted
     */
    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associateWith { true })
    }

    /**
     * Function to send granted result with callback which registered before or showing rationale
     * dialog if permissions result is not granted
     * Also, it sends result in detailed callback which registered whether if permissions result is Granted or not
     * Also, it clean up at the end
     */
    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        if (grantResults.all { it.value }) //Permissions are granted
            isGrantedCallback.invoke()
        else
            onPermissionsNotGranted()

        isGrantedDetailedCallback(grantResults.mapKeys { Permission.from(it.key) })
        cleanUp()
    }

    /**
     * Function to be called in permissions isn't granted to check if should show rationale dialog or not
     */
    private fun onPermissionsNotGranted() {
        fragment.get()?.let {
            // Permission is denied, the user has rejected the request
            if (!shouldShowPermissionRationale(it)) {
                // Permission is denied, the user has checked the Don't ask again.
                displayRationale(it)
            }
        }
    }

    /**
     * Function to request required permissions to be shown to users to allow or deny it
     */
    private fun requestPermissions() {
        requestPermissions?.launch(getPermissionList())
    }

    /**
     * Function to check if all permissions are granted or not
     */
    private fun areAllPermissionsGranted(fragment: Fragment) =
        requiredPermissions.all { it.isGranted(fragment) }

    /**
     * Function to check if it show permission rationale to describe to user why should him grant permission or not
     */
    private fun shouldShowPermissionRationale(fragment: Fragment) =
        requiredPermissions.any { it.requiresRationale(fragment) }

    /**
     * Function to get required permissions list
     */
    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    /**
     * Extension Function to check if permission is granted or not
     */
    private fun Permission.isGranted(fragment: Fragment) =
        permissions.all { hasPermission(fragment, it) }

    /**
     * Extension Function to check if specific permission requires rationale dialog to be shown or not
     */
    private fun Permission.requiresRationale(fragment: Fragment) =
        permissions.any { fragment.shouldShowRequestPermissionRationale(it) }

    /**
     * Function to check if user granted a specific permission in his device or not
     */
    private fun hasPermission(fragment: Fragment, permission: String) =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Function to clean up after checking on permissions by clearing required permissions list
     * and clear registered callbacks and clear rationale message
     */
    private fun cleanUp() {
        requiredPermissions.clear()
        rationaleMessage = null
        isGrantedCallback = {}
        isGrantedDetailedCallback = {}
    }

    /**
     * Function to unregister permissions launcher
     */
    fun clean() {
        requestPermissions?.unregister()
    }
}