package com.competra.domain.models

data class CyclicParticipant(
    override val id: String,
    override val userId: String,
    val startPlace: Int,
    val startTime: Long,
    val group: String
): Participant
