package com.abanoub.photoweather.framework.presentation.services

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.abanoub.photoweather.framework.utils.Constants.Location.RECEIVER.INTENT_LOCATION
import com.google.android.gms.location.*

class LocationProvider {
    private lateinit var client: FusedLocationProviderClient
    private lateinit var callback: LocationCallback
    private lateinit var request: LocationRequest

    fun getLocation(context: Context){
        val intent = Intent(INTENT_LOCATION)

        client = LocationServices.getFusedLocationProviderClient(context)
        request = LocationRequest.create()

        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                intent.putExtra("longitude",location?.longitude)
                intent.putExtra("latitude",location?.latitude)

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}