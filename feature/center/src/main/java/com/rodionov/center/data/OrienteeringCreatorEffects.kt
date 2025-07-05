package com.rodionov.center.data

import com.rodionov.domain.models.ParticipantGroup

sealed class OrienteeringCreatorEffects {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup
    ): OrienteeringCreatorEffects()
}