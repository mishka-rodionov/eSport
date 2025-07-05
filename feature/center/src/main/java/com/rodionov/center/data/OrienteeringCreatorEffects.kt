package com.rodionov.center.data

import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

sealed class OrienteeringCreatorEffects {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup
    ): OrienteeringCreatorEffects()

    data object Apply: OrienteeringCreatorEffects()
    data class UpdateCompetitionDate(val competitionDate: LocalDate): OrienteeringCreatorEffects()
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorEffects()

}