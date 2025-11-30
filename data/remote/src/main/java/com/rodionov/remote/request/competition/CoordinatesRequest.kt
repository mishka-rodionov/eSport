package com.rodionov.remote.request.competition

import com.google.gson.annotations.SerializedName

data class CoordinatesRequest(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)
