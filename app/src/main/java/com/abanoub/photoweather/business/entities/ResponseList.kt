package com.abanoub.photoweather.business.entities

import com.google.gson.annotations.SerializedName

class ResponseList(
    @SerializedName("name") var name: String,
    @SerializedName("main") var main: ResponseMain,
    @SerializedName("weather") var weather: List<ResponseWeather>
)