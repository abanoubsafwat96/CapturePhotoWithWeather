package com.abanoub.photoweather.framework.datasources

import com.abanoub.photoweather.business.entities.Response
import com.abanoub.photoweather.framework.utils.Constants.Network.EndPoints.FIND
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET(FIND)
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") count: Int,
        @Query("appid") api_key: String
    ): Response
}