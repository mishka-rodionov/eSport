package com.rodionov.remote.response.competition

import com.google.gson.annotations.SerializedName
import com.rodionov.remote.response.competition.CoordinatesResponse

data class CompetitionResponse(
    @SerializedName("title")
    val title: String,

    @SerializedName("date")
    val date: Long, // timestamp

    @SerializedName("kindOfSport")
    val kindOfSport: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("coordinates")
    val coordinates: CoordinatesResponse
)