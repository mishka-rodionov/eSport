package com.rodionov.remote.response.events

import com.google.gson.annotations.SerializedName

data class ParticipantGroupDetailResponse(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("title") val title: String,
    @SerializedName("maxParticipants") val maxParticipants: Int?,
    @SerializedName("registeredCount") val registeredCount: Int
)

data class CompetitionDetailResponse(
    @SerializedName("remoteId") val remoteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("startDate") val startDate: Long,
    @SerializedName("endDate") val endDate: Long?,
    @SerializedName("kindOfSport") val kindOfSport: String,
    @SerializedName("description") val description: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("mainOrganizerId") val mainOrganizerId: String?,
    @SerializedName("status") val status: String,
    @SerializedName("registrationStart") val registrationStart: Long?,
    @SerializedName("registrationEnd") val registrationEnd: Long?,
    @SerializedName("maxParticipants") val maxParticipants: Int?,
    @SerializedName("resultsStatus") val resultsStatus: String,
    @SerializedName("participantGroups") val participantGroups: List<ParticipantGroupDetailResponse>
)
