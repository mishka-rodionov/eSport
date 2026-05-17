package com.competra.remote.request.orienteering

data class OrienteeringResultRequest(
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
    val splits: List<SplitTimeRequest>?,
    val isEditable: Boolean,
    val isEdited: Boolean,
    val serverUpdatedAt: Long? = null
)

data class SplitTimeRequest(
    val controlPoint: Int,
    val timestamp: Long
)
