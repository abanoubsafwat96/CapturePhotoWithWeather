package com.abanoub.photoweather.framework.presentation.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abanoub.photoweather.business.entities.ResponseList
import com.abanoub.photoweather.business.usecases.abstraction.WeatherUseCase
import com.abanoub.photoweather.framework.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(private val weatherUseCase: WeatherUseCase) : ViewModel() {

    private val _weatherDataState = MutableLiveData<DataState<String>>()
    val weatherDataState: LiveData<DataState<String>> get() = _weatherDataState

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherUseCase.getWeatherData(latitude, longitude)
                .onStart { _weatherDataState.value = DataState.Loading }
                .catch { _weatherDataState.value = DataState.Failure(it) }
                .collect { data ->

                    val responseList: ResponseList = data[0].list[0]
                    val weatherData = (responseList.name + " , " +
                            (responseList.main.temp.toDouble() - 273.15).roundToInt() +
                            "°C" + " , " + responseList.weather[0].main)

                    _weatherDataState.value = DataState.Success(weatherData)
                }
        }
    }
}