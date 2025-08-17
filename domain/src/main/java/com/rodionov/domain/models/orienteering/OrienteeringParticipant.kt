package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Participant

data class OrienteeringParticipant(
    override val id: String,
    override val userId: String,
    val group: String,
    val commandName: String,
    val startNumber: String,
    val chipNumber: String,
    val comment: String
): Participant