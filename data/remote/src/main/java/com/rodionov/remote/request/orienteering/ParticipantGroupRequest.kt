package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName

data class ParticipantGroupRequest(
    @SerializedName("groupId")
    val groupId: Long? = null, //может быть null при создании группы участников
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

