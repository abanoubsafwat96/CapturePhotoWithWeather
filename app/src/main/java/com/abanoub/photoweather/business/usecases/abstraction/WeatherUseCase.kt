package com.abanoub.photoweather.business.usecases.abstraction

import com.abanoub.photoweather.business.entities.Response
import kotlinx.coroutines.flow.Flow

interface WeatherUseCase {
    suspend fun getWeatherData(latitude: Double, longitude: Double): Flow<Response>
}