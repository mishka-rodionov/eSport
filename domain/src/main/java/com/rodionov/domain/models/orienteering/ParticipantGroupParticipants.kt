package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ParticipantGroup

data class ParticipantGroupParticipants(
    val group: ParticipantGroup,
    val participants: List<OrienteeringParticipant>
)
