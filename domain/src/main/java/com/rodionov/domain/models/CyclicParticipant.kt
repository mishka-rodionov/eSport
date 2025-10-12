package com.rodionov.domain.models

data class CyclicParticipant(
    override val id: Long,
    override val userId: String,
    val startPlace: Int,
    val startTime: Long,
    val group: String
): Participant
