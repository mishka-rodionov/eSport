package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName

data class ParticipantGroupResponse(
    @SerializedName("groupId")
    val groupId: Long,
    @SerializedName("competitionId")
    val competitionId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("countOfControls")
    val countOfControls: Int,
    @SerializedName("maxTimeInMinute")
    val maxTimeInMinute: Int
)

