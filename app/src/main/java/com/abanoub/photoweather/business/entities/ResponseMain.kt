package com.abanoub.photoweather.business.entities

import com.google.gson.annotations.SerializedName

class ResponseMain(
    @SerializedName("temp") var temp: String
)