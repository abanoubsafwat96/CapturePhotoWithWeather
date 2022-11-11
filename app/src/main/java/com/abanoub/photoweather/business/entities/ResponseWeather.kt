package com.abanoub.photoweather.business.entities

import com.google.gson.annotations.SerializedName

class ResponseWeather(
    @SerializedName("main") val main: String
)