package com.rodionov.domain.models

data class OrienteeringParticipant(
    override val id: String,
    override val userId: String,
    val group: String,
    val commandName: String,
    val startNumber: String,
    val chipNumber: String,
    val comment: String
): Participant