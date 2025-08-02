package com.rodionov.remote.request.competition

import com.google.gson.annotations.SerializedName

data class CompetitionRequest(
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
    val coordinates: CoordinatesRequest
)
