package com.rodionov.remote.response.orienteering

data class OrienteeringResultResponse(
    val id: Long,
    val competitionId: Long,
    val groupId: Long,
    val participantId: String,
    val startTime: Long?,
    val finishTime: Long?,
    val totalTime: Long?,
    val rank: Int?,
    val status: String,
    val penaltyTime: Long,
    val splits: List<SplitTimeResponse>?,
    val isEditable: Boolean,
    val isEdited: Boolean
)

data class SplitTimeResponse(
    val controlPoint: Int,
    val timestamp: Long
)
