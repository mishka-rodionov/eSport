package com.rodionov.domain.models.orienteering

data class ParticipantWithResult(
    val participant: OrienteeringParticipant,
    val result: OrienteeringResult?
)
