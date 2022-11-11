package com.abanoub.photoweather.business.repositories.impl

import com.abanoub.photoweather.business.repositories.abstraction.WeatherRepository
import com.abanoub.photoweather.framework.datasources.WeatherApi
import com.abanoub.photoweather.framework.utils.Constants.Network.EndPoints.API_KEY
import com.abanoub.photoweather.framework.utils.Constants.Network.EndPoints.COUNT
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val weatherApi: WeatherApi) :
    WeatherRepository {
    override suspend fun getWeatherData(
        latitude: Double, longitude: Double
    ) = weatherApi.getWeatherData(latitude, longitude, COUNT, API_KEY)
}