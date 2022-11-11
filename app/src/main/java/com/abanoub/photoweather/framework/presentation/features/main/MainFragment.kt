package com.abanoub.photoweather.framework.presentation.features.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.abanoub.photoweather.R
import com.abanoub.photoweather.framework.presentation.services.MyLocationService
import com.abanoub.photoweather.databinding.FragmentMainBinding
import com.abanoub.photoweather.framework.presentation.features.base.BaseFragment
import com.abanoub.photoweather.framework.utils.Constants.Location.RECEIVER.INTENT_LOCATION
import com.abanoub.photoweather.framework.utils.DataState
import com.abanoub.photoweather.framework.utils.permissions.Permission
import com.abanoub.photoweather.framework.utils.permissions.PermissionManager
import com.abanoub.photoweather.framework.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class MainFragment @Inject constructor() : BaseFragment<FragmentMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private val permissionManager = PermissionManager.from(this)
    private lateinit var locationBroadcastReceiver: BroadcastReceiver

    override fun bindViews() {
        initUI()
        subscribeOnObservers()
        registerLocationBroadCastReceiver()
        checkLocationPermission()
    }

    private fun initUI() {
    }

    private fun subscribeOnObservers() {
        viewModel.weatherDataState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    activity?.showSnackBar(it.data)
                }
                is DataState.Failure -> {
                    activity?.showSnackBar(it.throwable.message.toString())
                }
                is DataState.Loading -> {}
            }
        }
    }

    private fun checkLocationPermission() {
        permissionManager
            .request(Permission.Location)
            .rationale(getString(R.string.you_should_grant_permission_to_continue))
            .checkPermission { startLocationService() }
    }

    private fun startLocationService() {
        activity?.startService(getLocationServiceIntent())
    }

    private fun getLocationServiceIntent() = Intent(context, MyLocationService::class.java)

    private fun registerLocationBroadCastReceiver() {
        locationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                val latitude = intent.getDoubleExtra("latitude", 0.0)
            }
        }
    }

    private fun stopLocationService() {
        activity?.stopService(getLocationServiceIntent())
    }

    override fun onStart() {
        super.onStart()
        if (::locationBroadcastReceiver.isInitialized)
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                locationBroadcastReceiver, IntentFilter(INTENT_LOCATION)
            )
    }

    override fun onStop() {
        super.onStop()
        if (::locationBroadcastReceiver.isInitialized)
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(locationBroadcastReceiver)
    }

    override fun getLayoutResId(): Int = R.layout.fragment_main

    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
        permissionManager.clean()
    }
}