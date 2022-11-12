package com.abanoub.photoweather.framework.presentation.services

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.abanoub.photoweather.framework.utils.Constants.Location.RECEIVER.INTENT_LOCATION
import com.google.android.gms.location.*

class LocationProvider {
    private lateinit var client: FusedLocationProviderClient

    fun getLocation(context: Context) {
        val intent = Intent(INTENT_LOCATION)

        client = LocationServices.getFusedLocationProviderClient(context)

        client.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.

            intent.putExtra("longitude", location?.longitude)
            intent.putExtra("latitude", location?.latitude)

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}