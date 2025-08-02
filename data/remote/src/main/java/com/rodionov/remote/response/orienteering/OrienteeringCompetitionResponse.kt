package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName
import com.rodionov.remote.response.competition.CompetitionResponse

data class OrienteeringCompetitionResponse(
    @SerializedName("competitionId")
    val competitionId: Long,

    @SerializedName("competition")
    val competition: CompetitionResponse,

    @SerializedName("direction")
    val direction: String // "FORWARD", "BY_CHOICE", etc.
)