package com.rodionov.remote.response.events

import com.google.gson.annotations.SerializedName

data class EventParticipantGroupResponse(
    @SerializedName("groupId")
    val groupId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("maxParticipant")
    val maxParticipant: Int,
    @SerializedName("registeredParticipant")
    val registeredParticipant: Int
)
