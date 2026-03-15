package com.rodionov.remote.response.events

import com.google.gson.annotations.SerializedName

data class CyclicEventDetailsResponse(
    @SerializedName("eventId")
    val eventId: Long,
    @SerializedName("organizationId")
    val organizationId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("participantGroups")
    val participantGroups: List<EventParticipantGroupResponse>
)
