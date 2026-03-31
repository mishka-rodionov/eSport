package com.rodionov.remote.request.orienteering

data class OrienteeringResultRequest(
    val id: Long,
    val competitionId: Long,
    val groupId: Long,
    val participantId: Long,
    val startTime: Long?,
    val finishTime: Long?,
    val totalTime: Long?,
    val status: String,
    val penaltyTime: Long,
    val splits: List<SplitTimeRequest>?,
    val isEditable: Boolean,
    val isEdited: Boolean
)

data class SplitTimeRequest(
    val controlPoint: Int,
    val timestamp: Long
)
