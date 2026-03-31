package com.rodionov.remote.request.orienteering

data class OrienteeringParticipantRequest(
    val id: Long,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val groupId: Long,
    val groupName: String,
    val competitionId: Long,
    val commandName: String,
    val startNumber: String,
    val startTime: Long,
    val chipNumber: String,
    val comment: String,
    val isChipGiven: Boolean
)
