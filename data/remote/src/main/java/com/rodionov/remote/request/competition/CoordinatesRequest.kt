package com.rodionov.remote.request.competition

import com.google.gson.annotations.SerializedName

data class CoordinatesRequest(
    @SerializedName("latitude")
    private val latitude: Double,

    @SerializedName("longitude")
    private val longitude: Double
)
