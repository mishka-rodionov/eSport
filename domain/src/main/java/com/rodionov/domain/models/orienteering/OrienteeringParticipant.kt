package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Participant

data class OrienteeringParticipant(
    override val id: Long,
    override val userId: String,
    val firstName: String,
    val lastName: String,
    val groupId: Long,
    val competitionId: Long,
    val commandName: String,
    val startNumber: String,
    val chipNumber: String,
    val comment: String
): Participant