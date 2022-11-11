package com.abanoub.photoweather.di

import com.abanoub.photoweather.business.repositories.abstraction.WeatherRepository
import com.abanoub.photoweather.business.repositories.impl.WeatherRepositoryImpl
import com.abanoub.photoweather.business.usecases.abstraction.WeatherUseCase
import com.abanoub.photoweather.business.usecases.impl.WeatherUseCaseImpl
import com.abanoub.photoweather.framework.datasources.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherRepository(weathersApi: WeatherApi): WeatherRepository =
        WeatherRepositoryImpl(weathersApi)

    @Provides
    @Singleton
    fun provideWeatherUseCase(weatherRepository: WeatherRepository): WeatherUseCase =
        WeatherUseCaseImpl(weatherRepository)
}