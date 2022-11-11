package com.abanoub.photoweather.business.repositories.impl

import com.abanoub.photoweather.business.repositories.abstraction.WeatherRepository
import com.abanoub.photoweather.framework.datasources.WeatherApi
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val weatherApi: WeatherApi) : WeatherRepository {
    override suspend fun getWeatherData() = weatherApi.getWeatherData()
}