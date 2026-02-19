package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ParticipantGroup

data class GroupWithParticipantsAndResults(
    val group: ParticipantGroup,
    val participants: List<ParticipantWithResult>
)
