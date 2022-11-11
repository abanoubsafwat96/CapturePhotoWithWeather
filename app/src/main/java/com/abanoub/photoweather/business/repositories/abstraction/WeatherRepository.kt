package com.abanoub.photoweather.business.repositories.abstraction

import com.abanoub.photoweather.business.entities.Response

interface WeatherRepository {
    suspend fun getWeatherData(latitude: Double, longitude: Double): Response
}