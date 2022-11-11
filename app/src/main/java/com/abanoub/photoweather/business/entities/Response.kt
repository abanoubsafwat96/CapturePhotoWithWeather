package com.abanoub.photoweather.business.entities

import com.google.gson.annotations.SerializedName

class Response(
    @SerializedName("list") var list: List<ResponseList>
)