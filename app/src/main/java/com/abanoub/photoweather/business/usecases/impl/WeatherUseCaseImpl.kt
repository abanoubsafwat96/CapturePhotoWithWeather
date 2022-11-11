package com.abanoub.photoweather.business.usecases.impl

import com.abanoub.photoweather.business.entities.Response
import com.abanoub.photoweather.business.repositories.abstraction.WeatherRepository
import com.abanoub.photoweather.business.usecases.abstraction.WeatherUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherUseCaseImpl @Inject constructor(private val weatherRepository: WeatherRepository) :
    WeatherUseCase {
    override suspend fun getWeatherData(): Flow<List<Response>> = flow {
        emit(weatherRepository.getWeatherData())
    }.flowOn(Dispatchers.IO)
}