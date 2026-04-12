package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName

data class ParticipantGroupPublishRequest(
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("competitionId") val competitionId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("gender") val gender: String?,
    @SerializedName("minAge") val minAge: Int?,
    @SerializedName("maxAge") val maxAge: Int?,
    @SerializedName("distanceId") val distanceId: String,
    @SerializedName("maxParticipants") val maxParticipants: Int?
)
