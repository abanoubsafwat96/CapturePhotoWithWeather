package com.abanoub.photoweather.framework.datasources

import com.abanoub.photoweather.business.entities.Response
import retrofit2.http.GET

interface WeatherApi {
    @GET()
    suspend fun getWeatherData(): List<Response>
}