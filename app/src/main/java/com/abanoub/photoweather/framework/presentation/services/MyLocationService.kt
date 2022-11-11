package com.abanoub.photoweather.framework.presentation.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyLocationService : Service(){

    override fun onCreate() {
        super.onCreate()
        LocationProvider().getLocation(applicationContext)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}