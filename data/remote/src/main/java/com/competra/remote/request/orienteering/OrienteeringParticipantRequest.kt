package com.competra.remote.request.orienteering

data class OrienteeringParticipantRequest(
    val id: String,
    val userId: String?,
    val firstName: String,
    val lastName: String,
    val groupId: Long,
    val groupName: String,
    val competitionId: String,
    val commandName: String?,
    val startNumber: Int,
    val startTime: Long,
    val chipNumber: Long,
    val comment: String?,
    val isChipGiven: Boolean,
    val serverUpdatedAt: Long? = null
)
