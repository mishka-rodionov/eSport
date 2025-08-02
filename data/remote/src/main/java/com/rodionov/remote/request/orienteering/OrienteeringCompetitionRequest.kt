package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName
import com.rodionov.remote.request.competition.CompetitionRequest

data class OrienteeringCompetitionRequest(
    @SerializedName("competitionId")
    val competitionId: Long,

    @SerializedName("competition")
    val competition: CompetitionRequest,

    @SerializedName("direction")
    val direction: String // "FORWARD", "BY_CHOICE", "MARKING"
)
