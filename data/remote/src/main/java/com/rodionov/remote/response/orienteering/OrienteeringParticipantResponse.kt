package com.rodionov.remote.response.orienteering

data class OrienteeringParticipantResponse(
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
